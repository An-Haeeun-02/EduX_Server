package com.Capstone.EduX.examResult;

import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamInfoRepository examInfoRepository;

    public ExamResultService(ExamResultRepository examResultRepository, ExamInfoRepository examInfoRepository) {
        this.examResultRepository = examResultRepository;
        this.examInfoRepository = examInfoRepository;
    }

    //저장된 답안이 있는지 여부 판단
    public List<Map<String, Object>> getUserAnswers(Long examId, Long userId) {
        List<ExamResult> results = examResultRepository.findByExamInfoIdAndUserId(examId, userId);

        //저장된 답안이 없을때
        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "저장된 답안이 없습니다.");
        }

        // Map으로 응답 만들기 (원하는 데이터만 뽑기)
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", result.getId());
            map.put("examInfoID", result.getExamInfo().getId());
            map.put("examQuestionId", result.getExamQuestionId());
            map.put("userAnswer", result.getUserAnswer());
            return map;

        }).collect(Collectors.toList());
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

}
