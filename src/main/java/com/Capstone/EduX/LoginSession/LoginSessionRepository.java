package com.Capstone.EduX.LoginSession;

import com.Capstone.EduX.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSession, String> {
    Optional<LoginSession> findByStudent(Student student);
}
