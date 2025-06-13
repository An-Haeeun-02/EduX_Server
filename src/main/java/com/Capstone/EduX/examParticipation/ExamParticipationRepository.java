package com.Capstone.EduX.examParticipation;

import com.Capstone.EduX.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamParticipationRepository extends JpaRepository<ExamParticipation, Long> {
    @Query("SELECT ep.student FROM ExamParticipation ep WHERE ep.exam.id = :examId")
    List<Student> findStudentsByExamId(@Param("examId") Long examId);

    @Modifying
    @Query("DELETE FROM ExamParticipation ep WHERE ep.student.id = :studentId AND ep.exam.id = :examId")
    void deleteByStudentIdAndExamId(@Param("studentId") Long studentId, @Param("examId") Long examId);

}
