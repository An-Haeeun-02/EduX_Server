package com.Capstone.EduX.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsBystudentId(String studentId);

    boolean existsByNameAndStudentNumberAndPhoneNumber(String name, Long studentNumber, String phoneNumber);

    Optional<Student> findBystudentId(String userId);

    Student findByStudentId(String studentId);
}


