package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClassroomRepository extends JpaRepository<StudentClassroom, Long> {

    @Query("""
        SELECT sc.classroomID
        FROM StudentClassroom sc
        WHERE sc.student.studentId = :studentId
          AND sc.isConnected = true
    """)
    List<Classroom> findClassroomsByStudentId(@Param("studentId") String studentId);

    boolean existsByStudentAndClassroomID(Student student, Classroom classroom);

}
