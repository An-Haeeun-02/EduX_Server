package com.Capstone.EduX.gradingResult;

import com.Capstone.EduX.examQuestion.ExamQuestion;
import com.Capstone.EduX.examQuestion.ExamQuestionService;
import com.Capstone.EduX.examResult.ExamResult;
import com.Capstone.EduX.examResult.ExamResultRepository;
import com.Capstone.EduX.examResult.ExamResultService;
import com.Capstone.EduX.score.Score;
import com.Capstone.EduX.score.ScoreRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
public class GradingResultService {

    private final GradingResultRepository gradingResultRepository;
    private final ExamQuestionService examQuestionService;
    private final ExamResultRepository examResultRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final ExamResultService examResultService;

    public GradingResultService(GradingResultRepository gradingResultRepository,
                                ExamQuestionService examQuestionService,
                                ExamResultRepository examResultRepository,
                                ScoreRepository scoreRepository,
                                StudentRepository studentRepository,
                                ExamResultService examResultService) {
        this.gradingResultRepository = gradingResultRepository;
        this.examQuestionService = examQuestionService;
        this.examResultRepository = examResultRepository;
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
        this.examResultService = examResultService;
    }

    public List<Map<String, Object>> getStudentExamAnswers(String name, String studentNumber, Long examId) {
        Student student = studentRepository.findByNameAndStudentNumber(name, Long.parseLong(studentNumber))
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        List<ExamResult> results = examResultRepository.findByUserIdAndExamInfoId(student.getId(), examId);
        List<Map<String, Object>> list = new ArrayList<>();

        for (ExamResult result : results) {
            ExamQuestion question = examQuestionService.findById(result.getExamQuestionId());

            Map<String, Object> map = new HashMap<>();
            map.put("examResultId", result.getId());
            map.put("questionText", question.getQuestion());
            map.put("questionId", question.getId());
            map.put("studentAnswer", result.getUserAnswer());
            map.put("questionScore", question.getQuestionScore());
            map.put("type", question.getType());

            // 객관식 정답은 인덱스로 변환해서 내려줌
            if ("multiple".equalsIgnoreCase(question.getType())) {
                Object rawAnswer = question.getAnswer();
                int correctIndex = -1;

                // 정답이 List 형태일 경우
                if (rawAnswer instanceof List<?> rawList && !rawList.isEmpty()) {
                    try {
                        correctIndex = Integer.parseInt(String.valueOf(rawList.get(0)));
                    } catch (Exception e) {
                        correctIndex = -1;
                    }
                }
                map.put("correctAnswer", String.valueOf(correctIndex));
            } else {
                map.put("correctAnswer", question.getAnswer());
            }
            list.add(map);
        }
        return list;
    }

    public void gradeWithScore(String examQuestionId, Long examResultId, int inputScore) {
        // 문제 정보 조회 (최대 점수 확인용)
        ExamQuestion question = examQuestionService.findById(examQuestionId);
        int maxScore = question.getQuestionScore();
        int score = Math.max(0, Math.min(inputScore, maxScore)); // 최대 점수 제한

        // examResultId + examQuestionId 조합으로 중복 여부 확인
        Optional<GradingResult> existingGr = gradingResultRepository
                .findByExamResultIdAndExamQuestionId(examResultId, examQuestionId);

        GradingResult gr = existingGr.orElse(
                new GradingResult(examQuestionId, examResultId, 0)
        );

        gr.setScorePerQuestion(score);
        gradingResultRepository.save(gr);
        examResultService.updateIsGradeFlag(examResultId);
    }

    public boolean isAllGraded(Long examResultId) {
        // examResultId 하나로부터 userId와 examId 추출
        ExamResult anyResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new RuntimeException("해당 시험 결과를 찾을 수 없습니다."));

        Long userId = anyResult.getUserId();
        Long examId = anyResult.getExamInfo().getId();

        // 해당 학생의 해당 시험에 대한 모든 응답
        List<ExamResult> allResults = examResultRepository.findByUserIdAndExamInfoId(userId, examId);

        // 채점된 결과 수 세기
        int gradedCount = 0;
        for (ExamResult result : allResults) {
            List<GradingResult> grs = gradingResultRepository.findByExamResultId(result.getId());
            boolean hasGrading = grs.stream()
                    .anyMatch(g -> g.getExamQuestionId().equals(result.getExamQuestionId()));
            if (hasGrading) gradedCount++;
        }

        System.out.println("사용자 응답 수 (expected): " + allResults.size());
        System.out.println(" 채점 완료된 문제 수: " + gradedCount);

        return gradedCount == allResults.size();
    }



    @Transactional
    public void updateTotalScore(Long examResultId) {
        ExamResult anyResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 시험 결과를 찾을 수 없습니다."
                ));

        Long userId = anyResult.getUserId();
        Long examId = anyResult.getExamInfo().getId();

        // 해당 학생의 이 시험 전체 응답 가져오기 및 채점결과 합계
        int totalScore = examResultRepository
                .findByUserIdAndExamInfoId(userId, examId)
                .stream()
                .flatMap(er -> gradingResultRepository.findByExamResultId(er.getId()).stream())
                .mapToInt(GradingResult::getScorePerQuestion)
                .sum();

        // Score가 없으면 생성
        Score score = scoreRepository.findByStudentIdAndExamInfoId(userId, examId)
                .orElseGet(() -> {
                    Score newScore = new Score();
                    Student student = studentRepository.findById(userId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다."
                            ));
                    newScore.setStudent(student);
                    newScore.setExamInfo(anyResult.getExamInfo());
                    newScore.setScore(0);
                    return scoreRepository.save(newScore);
                });

        score.setScore(totalScore);
        scoreRepository.save(score);

        System.out.println("updateTotalScore 호출됨! examResultId = " + examResultId);
        System.out.println("총합 계산: " + totalScore);
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGradingStatus(Long examId, Long studentId) {
        // 1) 해당 학생·시험의 모든 ExamResult 조회
        List<ExamResult> results = examResultRepository
                .findByUserIdAndExamInfoId(studentId, examId);

        List<Map<String, Object>> list = new ArrayList<>();
        if (results == null || results.isEmpty()) {
            return list;
        }

        // 2) 각 ExamResult별로 자동채점 상태(status)와 점수(score)를 계산
        for (ExamResult er : results) {
            String qid = er.getExamQuestionId();

            // 잘못된 ID는 건너뛰기
            if (qid == null || qid.isEmpty() || "undefined".equals(qid)) {
                log.warn("스킵: 잘못된 examQuestionId={}", qid);
                continue;
            }

            try {
                // 문항 정보 조회
                ExamQuestion question = examQuestionService.findById(qid);

                // 저장된 GradingResult 중 해당 문항의 레코드 찾기
                Optional<GradingResult> grOpt = gradingResultRepository
                        .findByExamResultId(er.getId()).stream()
                        .filter(gr -> qid.equals(gr.getExamQuestionId()))
                        .findFirst();

                // 상태 심볼과 점수 계산
                String symbol = null;
                Integer score = null;
                if (grOpt.isPresent()) {
                    score = grOpt.get().getScorePerQuestion();
                    Integer maxScore = question.getQuestionScore();
                    if (score != null && maxScore != null) {
                        if (score.equals(maxScore))      symbol = "O";
                        else if (score == 0)             symbol = "X";
                        else                             symbol = "△";
                    }
                }

                // 결과 맵 생성
                Map<String, Object> map = new HashMap<>();
                map.put("questionNumber", question.getNumber());
                map.put("questionId",       question.getId());
                map.put("status",           symbol);
                map.put("score",            score);

                list.add(map);

            } catch (Exception ex) {
                log.warn("스킵: 문항 조회 실패 qid={} error={}", qid, ex.getMessage());
            }
        }

        return list;
    }

    @Transactional
    public void autoGradeForStudent(String name, String studentNumber, Long examId) {
        // 1) 학번 파싱 & 학생 조회
        Long sid;
        try {
            sid = Long.parseLong(studentNumber);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "잘못된 학번 형식입니다."
            );
        }
        Student student = studentRepository.findByNameAndStudentNumber(name, sid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다."
                ));

        // 2) 해당 학생의 답안 전체 조회
        List<ExamResult> results =
                examResultRepository.findByUserIdAndExamInfoId(student.getId(), examId);
        if (results.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "채점 대상 답안이 없습니다."
            );
        }

        // 3) 문항별 자동채점
        for (ExamResult result : results) {
            try {
                ExamQuestion question = examQuestionService.findById(result.getExamQuestionId());
                String type = question.getType().toLowerCase();

                // — 주관식(및 기타) 문항은 건너뛰기 —
                if (!"multiple".equals(type) && !"ox".equals(type)) {
                    log.info("주관식 문항 자동채점 제외: examResultId={}, type={}", result.getId(), type);
                    continue;
                }

                // — multiple/ox 채점 —
                int score = 0;
                if ("multiple".equals(type)) {
                    // 1) 정답 인덱스 계산
                    Object rawAnswer = question.getAnswer();
                    int oneBased = (rawAnswer instanceof List<?> list && !list.isEmpty())
                            ? Integer.parseInt(String.valueOf(list.get(0)))
                            : Integer.parseInt(String.valueOf(rawAnswer));
                    int correctIndex = oneBased - 1;

                    // 2) 학생 답안 파싱, 실패하면 -1(절대 일치 불가) 처리
                    int studentIdx;
                    try {
                        studentIdx = Integer.parseInt(
                                Optional.ofNullable(result.getUserAnswer()).orElse("")
                        );
                    } catch (NumberFormatException ex) {
                        studentIdx = -1;
                    }

                    // 3) 일치할 때만 점수 부여
                    if (studentIdx == correctIndex) {
                        score = question.getQuestionScore();
                    }

                } else if ("ox".equals(type)) {
                    // 1) 정답 텍스트
                    Object rawAnswer = question.getAnswer();
                    String correctText = (rawAnswer instanceof List<?> list && !list.isEmpty())
                            ? String.valueOf(list.get(0))
                            : String.valueOf(rawAnswer);

                    // 2) 학생 답안이 빈(null/“”) 이 아니고, 정답과 일치하면 점수
                    String ua = Optional.ofNullable(result.getUserAnswer()).orElse("").trim();
                    if (!ua.isEmpty() && correctText.equalsIgnoreCase(ua)) {
                        score = question.getQuestionScore();
                    }
                }

                // — GradingResult 항상 생성/갱신 —
                Optional<GradingResult> existingGr = gradingResultRepository
                        .findByExamResultIdAndExamQuestionId(result.getId(), result.getExamQuestionId());
                GradingResult gr = existingGr.orElse(
                        new GradingResult(result.getExamQuestionId(), result.getId(), 0)
                );
                gr.setScorePerQuestion(score);
                gradingResultRepository.save(gr);

                // — isGrade 플래그 업데이트 —
                examResultService.updateIsGradeFlag(result.getId());

            } catch (Exception ex) {
                log.warn("문항 자동채점 실패: examResultId={} error={}", result.getId(), ex.getMessage());
            }
        }

        // 6) 모든 문항이 채점되었으면 총점 업데이트
        try {
            if (isAllGraded(results.get(0).getId())) {
                ExamResult first = results.get(0);
                scoreRepository.findByStudentIdAndExamInfoId(student.getId(), examId)
                        .orElseGet(() -> {
                            Score newScore = new Score();
                            newScore.setStudent(student);
                            newScore.setExamInfo(first.getExamInfo());
                            newScore.setScore(0);
                            return scoreRepository.save(newScore);
                        });
                updateTotalScore(first.getId());
            }
        } catch (Exception ex) {
            log.warn("총점 업데이트 실패: student={} examId={} error={}",
                    student.getId(), examId, ex.getMessage());
        }
    }


    public Integer getTotalScore(String name, String studentNumber, Long examId) {
        Student student = studentRepository.findByNameAndStudentNumber(name, Long.parseLong(studentNumber))
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        return scoreRepository.findByStudentIdAndExamInfoId(student.getId(), examId)
                .map(Score::getScore)
                .orElse(0);
    }

    @Transactional
    public void ensureScoreExists(Long examResultId) {
        ExamResult examResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new RuntimeException("시험 결과를 찾을 수 없습니다."));
        Long studentId = examResult.getUserId();
        Long examId = examResult.getExamInfo().getId();

        if (scoreRepository.findByStudentIdAndExamInfoId(studentId, examId).isEmpty()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));
            Score score = new Score();
            score.setStudent(student);
            score.setExamInfo(examResult.getExamInfo());
            score.setScore(0);
            scoreRepository.save(score);
        }
    }
}
