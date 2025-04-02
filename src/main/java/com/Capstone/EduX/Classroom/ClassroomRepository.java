package com.Capstone.EduX.Classroom;

import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Classroom findByAccessCode(String accessCode);
}



