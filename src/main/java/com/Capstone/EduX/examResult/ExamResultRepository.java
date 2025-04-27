package com.Capstone.EduX.examResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // 시험 ID와 사용자 ID로 조회
    List<ExamResult> findByExamInfoIdAndUserId(Long examId, Long userId);

    //시험 저장(단일)
    Optional<ExamResult> findByExamInfoIdAndUserIdAndExamQuestionId(Long examInfoId, Long userId, String examQuestionId);
}