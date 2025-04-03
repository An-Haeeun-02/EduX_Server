package com.Capstone.EduX.examInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamInfoRepository extends JpaRepository<ExamInfo, Long> {

    @Query("""
        SELECT e
        FROM ExamInfo e
        WHERE e.classroom.id = :classroomId
          AND e.duration >= 6
    """)
    //duration ≥ 현재시간
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