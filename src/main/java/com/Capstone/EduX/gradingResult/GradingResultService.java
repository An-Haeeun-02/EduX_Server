package com.Capstone.EduX.gradingResult;

import com.Capstone.EduX.examQuestion.ExamQuestion;
import com.Capstone.EduX.examQuestion.ExamQuestionService;
import com.Capstone.EduX.examResult.ExamResult;
import com.Capstone.EduX.examResult.ExamResultRepository;
import com.Capstone.EduX.score.Score;
import com.Capstone.EduX.score.ScoreRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
class GradingResultService {

    private final GradingResultRepository gradingResultRepository;
    private final ExamQuestionService examQuestionService;
    private final ExamResultRepository examResultRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public GradingResultService(GradingResultRepository gradingResultRepository,
                                ExamQuestionService examQuestionService,
                                ExamResultRepository examResultRepository,
                                ScoreRepository scoreRepository,
                                StudentRepository studentRepository) {
        this.gradingResultRepository = gradingResultRepository;
        this.examQuestionService = examQuestionService;
        this.examResultRepository = examResultRepository;
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
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

        for (ExamResult er : results) {
            ExamQuestion question = examQuestionService.findById(er.getExamQuestionId());
            Optional<GradingResult> grOpt = gradingResultRepository.findByExamResultId(er.getId()).stream()
                    .filter(gr -> gr.getExamQuestionId().equals(er.getExamQuestionId()))
                    .findFirst();

            String symbol = null;
            if (grOpt.isPresent()) {
                int score = grOpt.get().getScorePerQuestion();
                int maxScore = question.getQuestionScore();

                if (score == maxScore) symbol = "O";
                else if (score == 0) symbol = "X";
                else symbol = "△";
            }

            Map<String, Object> map = new HashMap<>();
            map.put("questionNumber", question.getNumber());
            map.put("questionId", question.getId());
            map.put("status", symbol);
            list.add(map);
        }

        return list;
    }

    public void autoGradeForStudent(String name, String studentNumber, Long examId) {
        // 학생 정보 조회
        Student student = studentRepository.findByNameAndStudentNumber(name, Long.parseLong(studentNumber))
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        // 해당 학생이 본 시험의 모든 응답 조회
        List<ExamResult> results = examResultRepository.findByUserIdAndExamInfoId(student.getId(), examId);

        for (ExamResult result : results) {
            ExamQuestion question = examQuestionService.findById(result.getExamQuestionId());

            // 객관식일 경우 자동 채점
            if ("객관식".equals(question.getType())) {
                int score = result.getUserAnswer().equals(question.getAnswer())
                        ? question.getQuestionScore()
                        : 0;

                // 정확한 examResultId + examQuestionId 조합으로 중복 여부 확인
                Optional<GradingResult> existingGr = gradingResultRepository
                        .findByExamResultIdAndExamQuestionId(result.getId(), result.getExamQuestionId());

                GradingResult gr = existingGr.orElse(
                        new GradingResult(result.getExamQuestionId(), result.getId(), 0)
                );

                gr.setScorePerQuestion(score);
                gradingResultRepository.save(gr);
            }
        }

        // 모든 문제 채점 완료 시 총점 계산
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
