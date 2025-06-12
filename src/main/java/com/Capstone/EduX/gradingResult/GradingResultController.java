package com.Capstone.EduX.gradingResult;

import com.Capstone.EduX.examQuestion.ExamQuestion;
import com.Capstone.EduX.examQuestion.ExamQuestionService;
import com.Capstone.EduX.examResult.ExamResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grading")
@Slf4j
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
    public ResponseEntity<?> autoGradeObjectiveAnswers(@RequestParam String name,
                                                       @RequestParam String studentNumber,
                                                       @RequestParam Long examId) {
        log.debug("▶ 자동채점 API 호출됨: name={}, studentNumber={}, examId={}", name, studentNumber, examId);

        try {
            gradingResultService.autoGradeForStudent(name, studentNumber, examId);
            return ResponseEntity.ok("자동 채점 완료");
        } catch (Exception e) {
            log.error("❌ 자동채점 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
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
        try {
            log.info("▶ full 시작: examId={}, studentId={}", examId, studentId);

            // 1) 문제 전체
            List<ExamQuestion> qs = examQuestionService.findByExamId(examId);
            if (qs == null) qs = Collections.emptyList();

            // 2) 학생 답안
            List<Map<String,Object>> ua = examResultService.getUserAnswers(examId, studentId);
            if (ua == null) ua = Collections.emptyList();
            Map<Object, Map<String,Object>> ansMap = ua.stream()
                    .filter(m -> m.get("questionId") != null || m.get("examQuestionId") != null)
                    .collect(Collectors.toMap(
                            m -> m.getOrDefault("questionId", m.get("examQuestionId")),
                            m -> m,
                            (first, second) -> second
                    ));

            // 3) 자동채점 결과
            List<Map<String,Object>> gl = gradingResultService.getGradingStatus(examId, studentId);
            if (gl == null) gl = Collections.emptyList();
            Map<Object, Map<String,Object>> scoreMap = gl.stream()
                    .filter(m -> m.get("questionId") != null)
                    .collect(Collectors.toMap(
                            m -> m.get("questionId"),
                            m -> m,
                            (a,b) -> b
                    ));

            // 4) 합치기
            List<Map<String,Object>> full = new ArrayList<>();
            for (ExamQuestion q : qs) {
                Map<String,Object> m = new HashMap<>();

                // 보기 옵션
                List<String> opts = q.getDistractor() != null
                        ? q.getDistractor()
                        : Collections.emptyList();

                // 정답 텍스트 및 인덱스 계산 (1-based → 0-based)
                Object rawAnswer = q.getAnswer();
                String correctText = "";
                int correctIndex = -1;

                if ("multiple".equals(q.getType())) {
                    try {
                        int oneBased = 0;
                        if (rawAnswer instanceof List<?> list && !list.isEmpty()) {
                            oneBased = Integer.parseInt(String.valueOf(list.get(0)));
                        } else {
                            oneBased = Integer.parseInt(String.valueOf(rawAnswer));
                        }
                        correctIndex = oneBased - 1;  // 0-based 변환
                        if (correctIndex >= 0 && correctIndex < opts.size()) {
                            correctText = opts.get(correctIndex);
                        }
                    } catch (Exception e) {
                        log.warn("객관식 정답 인덱스 오류: rawAnswer={}", rawAnswer);
                    }
                } else {
                    correctText = rawAnswer != null ? rawAnswer.toString() : "";
                }

                // 자동채점 점수
                int score = 0;
                Map<String,Object> sc = scoreMap.get(q.getId());
                if (sc != null && sc.get("score") != null) {
                    try {
                        score = ((Number) sc.get("score")).intValue();
                    } catch (Exception e) {
                        log.warn("점수 변환 실패: {}", sc.get("score"));
                    }
                }

                // 기본 필드
                m.put("questionId",    q.getId());
                m.put("questionText",  q.getQuestion());
                m.put("type",          q.getType());
                m.put("options",       opts);
                m.put("correctAnswer", correctText);
                if ("multiple".equals(q.getType())) {
                    m.put("correctIndex", correctIndex);
                }
                m.put("maxScore",      q.getQuestionScore());
                m.put("autoScore",     score);

                // 학생 답안 병합
                Map<String,Object> uaEntry = ansMap.get(q.getId());
                if (uaEntry != null) {
                    m.put("examResultId", uaEntry.get("examResultId"));
                    m.put("studentAnswer", uaEntry.get("userAnswer"));
                    m.put("isGrade", uaEntry.getOrDefault("isGrade", 0));
                } else {
                    m.put("isGrade", 0);
                }

                full.add(m);
            }

            return ResponseEntity.ok(full);

        } catch (Exception e) {
            log.error("▶ full 에러", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}