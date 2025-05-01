package com.Capstone.EduX.gradingResult;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface GradingResultRepository extends MongoRepository<GradingResult, String> {
    List<GradingResult> findByExamResultId(Long examResultId);
    List<GradingResult> findByExamQuestionId(String examQuestionId);
    // GradingResultRepository.java
    Optional<GradingResult> findByExamResultIdAndExamQuestionId(Long examResultId, String examQuestionId);

}

