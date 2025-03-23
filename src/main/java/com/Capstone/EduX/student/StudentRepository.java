package com.Capstone.EduX.student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsByUserId(String userId);

    boolean existsByNameAndStudentNumberAndPhoneNumber(String name, Long studentNumber, String phoneNumber);

}


