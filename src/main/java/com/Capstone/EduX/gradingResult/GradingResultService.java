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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                .orElseThrow(() -> new RuntimeException("해당 시험 결과를 찾을 수 없습니다."));

        Long userId = anyResult.getUserId();
        Long examId = anyResult.getExamInfo().getId();

        // 해당 학생의 이 시험 전체 응답 가져오기
        List<ExamResult> allResults = examResultRepository.findByUserIdAndExamInfoId(userId, examId);

        // 전체 채점결과 모으기
        List<GradingResult> allGradingResults = new ArrayList<>();
        for (ExamResult result : allResults) {
            allGradingResults.addAll(gradingResultRepository.findByExamResultId(result.getId()));
        }

        int totalScore = allGradingResults.stream()
                .mapToInt(GradingResult::getScorePerQuestion)
                .sum();

        Score score = scoreRepository.findByStudentIdAndExamInfoId(userId, examId)
                .orElseThrow(() -> new RuntimeException("Score 엔티티를 찾을 수 없습니다."));

        score.setScore(totalScore);
        scoreRepository.save(score);

        System.out.println("updateTotalScore 호출됨! examResultId = " + examResultId);
        System.out.println("총합 계산: " + totalScore);
    }


    public List<Map<String, Object>> getGradingStatus(Long examId, Long studentId) {
        List<ExamResult> results = examResultRepository.findByUserIdAndExamInfoId(studentId, examId);
        List<Map<String, Object>> list = new ArrayList<>();

        if (results == null) return list;

        for (ExamResult er : results) {
            ExamQuestion question = examQuestionService.findById(er.getExamQuestionId());
            if (question == null) continue;

            Optional<GradingResult> grOpt = gradingResultRepository.findByExamResultId(er.getId()).stream()
                    .filter(gr -> gr.getExamQuestionId() != null && gr.getExamQuestionId().equals(er.getExamQuestionId()))
                    .findFirst();

            String symbol = null;
            Integer score = null;

            if (grOpt.isPresent()) {
                GradingResult gr = grOpt.get();
                score = gr.getScorePerQuestion();

                Integer maxScore = question.getQuestionScore();
                if (score != null && maxScore != null) {
                    if (score.equals(maxScore)) symbol = "O";
                    else if (score == 0) symbol = "X";
                    else symbol = "△";
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("questionNumber", question.getNumber());
            map.put("questionId", question.getId());
            map.put("status", symbol);
            map.put("score", score); // null일 수 있음
            list.add(map);
        }

        return list;
    }

    @Transactional
    public void autoGradeForStudent(String name, String studentNumber, Long examId) {
        Student student = studentRepository.findByNameAndStudentNumber(
                name, Long.parseLong(studentNumber)
        ).orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        List<ExamResult> results = examResultRepository
                .findByUserIdAndExamInfoId(student.getId(), examId);

        for (ExamResult result : results) {
            ExamQuestion question = examQuestionService.findById(result.getExamQuestionId());
            String type = question.getType().toLowerCase();

            int score = 0;

            if ("multiple".equals(type)) {
                Object rawAnswer = question.getAnswer();
                int oneBased;
                if (rawAnswer instanceof List<?> list && !list.isEmpty()) {
                    oneBased = Integer.parseInt(String.valueOf(list.get(0)));
                } else {
                    oneBased = Integer.parseInt(String.valueOf(rawAnswer));
                }
                int correctIndex = oneBased - 1;
                int studentIdx = Integer.parseInt(result.getUserAnswer());
                if (studentIdx == correctIndex) {
                    score = question.getQuestionScore();
                }
                // 자동채점된 문제만 is_grade = 1로!
                examResultService.updateIsGradeFlag(result.getId());

            } else if ("ox".equals(type)) {
                Object rawAnswer = question.getAnswer();
                String correctText;
                if (rawAnswer instanceof List<?> list && !list.isEmpty()) {
                    correctText = String.valueOf(list.get(0));
                } else {
                    correctText = String.valueOf(rawAnswer);
                }
                String studentOX = result.getUserAnswer();
                if (correctText.equalsIgnoreCase(studentOX)) {
                    score = question.getQuestionScore();
                }
                // 자동채점된 문제만 is_grade = 1로!
                examResultService.updateIsGradeFlag(result.getId());
            }
            // 서술형 등 기타 타입은 자동채점/업데이트 하지 않음

            Optional<GradingResult> existingGr = gradingResultRepository
                    .findByExamResultIdAndExamQuestionId(
                            result.getId(), result.getExamQuestionId()
                    );
            GradingResult gr = existingGr.orElse(
                    new GradingResult(result.getExamQuestionId(), result.getId(), 0)
            );
            gr.setScorePerQuestion(score);
            gradingResultRepository.save(gr);
        }

        // 총점 계산 및 저장(이 부분은 이전과 동일하게 유지)
        if (!results.isEmpty() && isAllGraded(results.get(0).getId())) {
            ExamResult firstExamResult = results.get(0);

            // 총점이 없으면 새로 생성
            scoreRepository.findByStudentIdAndExamInfoId(student.getId(), examId)
                    .orElseGet(() -> {
                        Score newScore = new Score();
                        newScore.setStudent(student);
                        newScore.setExamInfo(firstExamResult.getExamInfo());
                        newScore.setScore(0);
                        return scoreRepository.save(newScore);
                    });

            // 총점 계산 및 저장
            updateTotalScore(firstExamResult.getId());
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
