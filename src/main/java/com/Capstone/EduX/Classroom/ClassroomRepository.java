package com.Capstone.EduX.Classroom;

import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Classroom findByAccessCode(String accessCode);
    // 교수 ID로 강의실 목록 조회 (교수가 생성한 모든 강의실 조회)
    List<Classroom> findAllByProfessorId(Long professorId);
}


