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
        SELECT sc.classroom
        FROM StudentClassroom sc
        WHERE sc.student.studentId = :studentId
          AND sc.isConnected = true
    """)
    List<Classroom> findClassroomsByStudentId(@Param("studentId") String studentId);

    @Query("""
        SELECT sc.student
        FROM StudentClassroom sc
        WHERE sc.classroom.id = :classroomId
          AND sc.isConnected = true
    """)
    List<Student> findStudentsByClassroomId(@Param("classroomId") Long classroomId);

    boolean existsByStudentAndClassroom(Student student, Classroom classroom);

    //해당 학생과 강의실 관계 조회
    StudentClassroom findByStudentAndClassroom(Student student, Classroom classroom);

    @Query("""
    SELECT sc.student.studentId, sc.student.name, s.studentTestStartTime, s.studentTestEndTime, s.score
    FROM StudentClassroom sc
    LEFT JOIN Score s ON sc.student = s.student AND sc.classroom = s.classroom
    WHERE sc.classroom.id = :classroomId
      AND sc.isConnected = true
""")
    List<Object[]> findStudentsWithExamInfoByClassroomId(@Param("classroomId") Long classroomId);

}
