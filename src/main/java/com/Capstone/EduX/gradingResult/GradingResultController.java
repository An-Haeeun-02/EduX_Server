package com.Capstone.EduX.gradingResult;

import com.Capstone.EduX.examQuestion.ExamQuestion;
import com.Capstone.EduX.examQuestion.ExamQuestionService;
import com.Capstone.EduX.examResult.ExamResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grading")

public class GradingResultController {

    private final GradingResultService gradingResultService;
    private final ExamQuestionService examQuestionService;
    private final ExamResultService examResultService;
    private static final Logger log = LoggerFactory.getLogger(GradingResultService.class);

    public GradingResultController(GradingResultService gradingResultService,
                                   ExamQuestionService examQuestionService,
                                   ExamResultService examResultService) {
        this.gradingResultService = gradingResultService;
        this.examQuestionService = examQuestionService;
        this.examResultService = examResultService;
    }

    //  학생의 이름 + 학번으로 해당 시험에 대한 문제 + 학생의 답안 불러오기
    @GetMapping("/view")
    public ResponseEntity<?> getExamResults(@RequestParam String name,
                                            @RequestParam String studentNumber,
                                            @RequestParam Long examId) {
        return ResponseEntity.ok(gradingResultService.getStudentExamAnswers(name, studentNumber, examId));
    }

    // 수동채점 결과 저장
    @PostMapping("/grade")
    public ResponseEntity<?> submitGrade(@RequestBody Map<String, Object> req) {
        String examQuestionId = req.get("examQuestionId").toString();
        Long examResultId = Long.valueOf(req.get("examResultId").toString());
        Integer scoreInput = Integer.parseInt(req.get("score").toString());

        gradingResultService.gradeWithScore(examQuestionId, examResultId, scoreInput);

        if (gradingResultService.isAllGraded(examResultId)) {
            gradingResultService.ensureScoreExists(examResultId);
            gradingResultService.updateTotalScore(examResultId);
        }

        return ResponseEntity.ok("채점 완료");
    }
    //현재 채점 현황 O,x,세모로 표시
    @GetMapping("/status")
    public ResponseEntity<?> getGradingStatus(
            @RequestParam Long examId,
            @RequestParam Long studentId) {

        List<Map<String, Object>> statusList = gradingResultService.getGradingStatus(examId, studentId);
        return ResponseEntity.ok(statusList);
    }
    //자동채점
    @PostMapping("/autograde")
    public ResponseEntity<?> autoGrade(
            @RequestParam String name,
            @RequestParam String studentNumber,
            @RequestParam Long examId
    ) {
        log.debug("▶ 자동채점 호출: name={}, studentNumber={}, examId={}", name, studentNumber, examId);
        try {
            gradingResultService.autoGradeForStudent(name, studentNumber, examId);
            return ResponseEntity.ok(Map.of("message", "자동채점 완료"));

        } catch (ResponseStatusException ex) {
            // getStatusCode()를 사용해서 HttpStatusCode를 가져옵니다.
            HttpStatusCode status = ex.getStatusCode();
            log.info("자동채점 처리: status={}, reason={}", status, ex.getReason());
            return ResponseEntity
                    .status(status)
                    .body(Map.of("error", ex.getReason()));

        } catch (Exception e) {
            log.error("❌ 자동채점 중 예기치 못한 오류", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }


    // 총점 조회
    @GetMapping("/total-score")
    public ResponseEntity<?> getTotalScore(@RequestParam String name,
                                           @RequestParam String studentNumber,
                                           @RequestParam Long examId) {
        Integer totalScore = gradingResultService.getTotalScore(name, studentNumber, examId);
        return ResponseEntity.ok(Map.of("totalScore", totalScore));
    }

    @GetMapping("/full")
    public ResponseEntity<?> getFull(
            @RequestParam Long examId,
            @RequestParam Long studentId
    ) {
        log.info("▶ full 시작: examId={}, studentId={}", examId, studentId);
        try {
            // 1) 모든 문항을 한 번에 로드
            List<ExamQuestion> qs = examQuestionService.findByExamId(examId);
            if (qs == null) qs = Collections.emptyList();

            // 2) 학생 답안
            List<Map<String,Object>> ua = examResultService.getUserAnswers(examId, studentId);
            if (ua == null) ua = Collections.emptyList();
            Map<String, Map<String,Object>> ansMap = ua.stream()
                    .filter(m -> m.get("questionId") != null)
                    .collect(Collectors.toMap(
                            m -> Objects.toString(m.get("questionId")),
                            m -> m,
                            (a, b) -> b
                    ));

            // 3) 자동채점 결과
            List<Map<String,Object>> gl = gradingResultService.getGradingStatus(examId, studentId);
            if (gl == null) gl = Collections.emptyList();
            Map<String, Map<String,Object>> scoreMap = gl.stream()
                    .filter(m -> m.get("questionId") != null)
                    .collect(Collectors.toMap(
                            m -> Objects.toString(m.get("questionId")),
                            m -> m,
                            (a, b) -> b
                    ));

            // 4) 합치기
            List<Map<String,Object>> full = new ArrayList<>();
            for (ExamQuestion q : qs) {
                Map<String,Object> m = new HashMap<>();
                m.put("questionId",    q.getId());
                m.put("questionText",  q.getQuestion());
                m.put("type",          q.getType());
                List<String> opts = q.getDistractor()!=null ? q.getDistractor() : List.of();
                m.put("options",       opts);

                // 정답 텍스트/인덱스 계산
                Object rawAnswer = q.getAnswer();
                String correctText = "";
                int correctIndex = -1;
                if ("multiple".equalsIgnoreCase(q.getType())) {
                    try {
                        int one = (rawAnswer instanceof List<?> list && !list.isEmpty())
                                ? Integer.parseInt(list.get(0).toString())
                                : Integer.parseInt(rawAnswer.toString());
                        correctIndex = one - 1;
                        if (correctIndex >= 0 && correctIndex < opts.size()) {
                            correctText = opts.get(correctIndex);
                        }
                    } catch (Exception e) {
                        log.warn("정답 인덱스 파싱 실패: {}", rawAnswer);
                    }
                    m.put("correctIndex", correctIndex);
                } else {
                    correctText = rawAnswer != null ? rawAnswer.toString() : "";
                }
                m.put("correctAnswer", correctText);
                m.put("maxScore",      q.getQuestionScore());

                // 자동채점 점수 병합
                Map<String,Object> sc = scoreMap.get(q.getId());
                int autoScore = sc!=null && sc.get("score") instanceof Number
                        ? ((Number)sc.get("score")).intValue()
                        : 0;
                m.put("autoScore", autoScore);

                // 학생답안 병합
                Map<String,Object> uaEntry = ansMap.get(q.getId());
                if (uaEntry != null) {
                    m.put("examResultId", uaEntry.get("examResultId"));
                    m.put("studentAnswer", uaEntry.get("userAnswer"));
                    m.put("isGrade",       uaEntry.getOrDefault("isGrade", 0));
                } else {
                    m.put("isGrade", 0);
                }

                full.add(m);
            }

            return ResponseEntity.ok(full);

        } catch (Exception e) {
            log.error("▶ full 에러", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "시험지 불러오기 중 오류가 발생했습니다."));
        }
    }


}