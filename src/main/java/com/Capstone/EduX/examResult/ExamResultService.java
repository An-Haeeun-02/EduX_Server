package com.Capstone.EduX.examResult;

import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import com.Capstone.EduX.gradingResult.GradingResultRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamInfoRepository examInfoRepository;
    private final GradingResultRepository gradingResultRepository;

    public ExamResultService(ExamResultRepository examResultRepository,
                             ExamInfoRepository examInfoRepository,
                             GradingResultRepository gradingResultRepository) {
        this.examResultRepository = examResultRepository;
        this.examInfoRepository = examInfoRepository;
        this.gradingResultRepository = gradingResultRepository;
    }

    //저장된 답안이 있는지 여부 판단
    public List<Map<String, Object>> getUserAnswers(Long examId, Long userId) {
        List<ExamResult> results = examResultRepository.findByExamInfoIdAndUserId(examId, userId);

        // 빈 리스트로 처리 (404 대신)
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(er -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("examResultId",   er.getId());
                    m.put("questionId",     er.getExamQuestionId());
                    m.put("userAnswer",     er.getUserAnswer());
                    m.put("isGrade", er.getIsGrade() != null ? er.getIsGrade() : 0);

                    return m;
                })
                .collect(Collectors.toList());
    }

    //단일 정답 저장
    public Long saveOrUpdateAnswer(Long examId, Long userId, String examQuestionId, String userAnswer) {
        // 시험 정보 가져오기
        ExamInfo examInfo = examInfoRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("시험 정보가 존재하지 않습니다."));

        // 기존 답안 있는지 확인
        Optional<ExamResult> existing = examResultRepository
                .findByExamInfoIdAndUserIdAndExamQuestionId(examId, userId, examQuestionId);

        ExamResult result;
        if (existing.isPresent()) {
            // 기존 답안 있으면 수정
            result = existing.get();
            result.setUserAnswer(userAnswer);
        } else {
            // 없으면 새로 생성
            result = new ExamResult();
            result.setExamInfo(examInfo);
            result.setUserId(userId);
            result.setExamQuestionId(examQuestionId);
            result.setUserAnswer(userAnswer);
        }

        // 저장 후 ID 반환
        ExamResult saved = examResultRepository.save(result);
        return saved.getId();
    }

    //복수 정답 저장
    public List<Long> saveMultipleAnswers(Long examId, Long userId, List<Map<String, Object>> answers) {
        ExamInfo examInfo = examInfoRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("시험 정보가 존재하지 않습니다."));

        List<Long> savedIds = new ArrayList<>();

        for (Map<String, Object> answerData : answers) {
            String examQuestionId = answerData.get("examQuestionId").toString();
            String userAnswer = answerData.get("userAnswer").toString();

            Optional<ExamResult> existing = examResultRepository
                    .findByExamInfoIdAndUserIdAndExamQuestionId(examId, userId, examQuestionId);

            ExamResult result;
            if (existing.isPresent()) {
                result = existing.get();
                result.setUserAnswer(userAnswer);
            } else {
                result = new ExamResult();
                result.setExamInfo(examInfo);
                result.setUserId(userId);
                result.setExamQuestionId(examQuestionId);
                result.setUserAnswer(userAnswer);
            }

            ExamResult saved = examResultRepository.save(result);
            savedIds.add(saved.getId());
        }

        return savedIds;
    }

    @Transactional
    public void updateIsGradeFlag(Long examResultId) {
        log.debug("✅ isGrade 업데이트 실행: examResultId={}", examResultId);

        ExamResult er = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new RuntimeException("해당 시험 결과를 찾을 수 없습니다."));

        boolean graded = gradingResultRepository.findByExamResultId(examResultId)
                .stream()
                .anyMatch(gr -> gr.getExamQuestionId() != null);

        er.setIsGrade(graded ? 1 : 0);
        examResultRepository.save(er);
    }

    public List<Map<String, Object>> getIsGradeListByStudentAndExam(Long userId, Long examId) {
        List<ExamResult> results = examResultRepository.findByUserIdAndExamInfoId(userId, examId);

        return results.stream().map(er -> {
            Map<String, Object> map = new HashMap<>();
            map.put("examResultId", er.getId());
            map.put("isGrade", er.getIsGrade());
            return map;
        }).collect(Collectors.toList());
    }

}

