package com.Capstone.EduX.examResult;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // 특정 시험에 대한 응시 결과 목록 조회
    List<ExamResult> findByExamInfoId(Long examInfoId);

    // 특정 학생이 응시한 특정 시험의 결과
    List<ExamResult> findByUserIdAndExamInfoId(Long userId, Long examInfoId);

    // 시험 ID와 사용자 ID로 조회
    List<ExamResult> findByExamInfoIdAndUserId(Long examId, Long userId);

    //시험 저장(단일)
    Optional<ExamResult> findByExamInfoIdAndUserIdAndExamQuestionId(Long examInfoId, Long userId, String examQuestionId);

    @Modifying
    @Query("DELETE FROM ExamResult e WHERE e.examInfo.id = :examId")
    void deleteByExamInfoId(@Param("examId") Long examId);

    @Modifying
    @Query("DELETE FROM ExamResult e WHERE e.examInfo.id = :examId AND e.userId = :studentId")
    void deleteByStudentIdAndExamId(@Param("studentId") Long studentId, @Param("examId") Long examId);

    //자동채점 후 isGrade=1로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE ExamResult e SET e.isGrade = 1 WHERE e.id = :examResultId")
    void updateIsGradeFlag(@Param("examResultId") Long examResultId);

}
