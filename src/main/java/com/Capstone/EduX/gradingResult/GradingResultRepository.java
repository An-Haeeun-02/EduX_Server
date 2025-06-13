package com.Capstone.EduX.gradingResult;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GradingResultRepository extends MongoRepository<GradingResult, String> {
    List<GradingResult> findByExamResultId(Long examResultId);
    List<GradingResult> findByExamQuestionId(String examQuestionId);
    // GradingResultRepository.java
    Optional<GradingResult> findByExamResultIdAndExamQuestionId(Long examResultId, String examQuestionId);
    void deleteByExamQuestionId(String examQuestionId);
    void deleteByExamQuestionIdIn(List<String> examQuestionIds);
    void deleteByStudentIdAndExamId(Long studentId, Long examId);

}

