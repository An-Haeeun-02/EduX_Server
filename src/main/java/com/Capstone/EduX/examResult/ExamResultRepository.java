package com.Capstone.EduX.examResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // 특정 시험에 대한 응시 결과 목록 조회
    List<ExamResult> findByExamInfoId(Long examInfoId);

    // 특정 학생이 응시한 특정 시험의 결과
    List<ExamResult> findByUserIdAndExamInfoId(Long userId, Long examInfoId);
}
