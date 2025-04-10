package com.Capstone.EduX.examQuestion;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ExamQuestionRepository extends MongoRepository<ExamQuestion, String> {
    List<ExamQuestion> findByExamId(Long examId); // 시험 ID로 문제들 조회


}