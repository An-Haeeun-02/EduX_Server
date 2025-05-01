package com.Capstone.EduX.examInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamInfoRepository extends JpaRepository<ExamInfo, Long> {

    Optional<ExamInfo> findById(Long id); // 시험 ID로 조회

    @Query("""
        SELECT e
        FROM ExamInfo e
        WHERE e.classroom.id = :classroomId
    """)
    List<ExamInfo> findActiveExamsByClassroomId(@Param("classroomId") Long classroomId,
                                                @Param("now") LocalDateTime now);

    @Query("""
    SELECT e
    FROM ExamInfo e
    WHERE e.classroom.id = :classroomId
      AND e.classroom.professor.id = :professorId
""")
    List<ExamInfo> findAllExamsByClassroomIdAndProfessorId(@Param("classroomId") Long classroomId, @Param("professorId") Long professorId);


}