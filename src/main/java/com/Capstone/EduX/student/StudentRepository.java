package com.Capstone.EduX.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsBystudentId(String studentId);

    boolean existsByNameAndStudentNumberAndEmail(String name, Long studentNumber, String email);

    Optional<Student> findBystudentId(String userId);

    Student findByStudentId(String studentId);
}


