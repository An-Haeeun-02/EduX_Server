package com.Capstone.EduX.student;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentJoinService {
    private final StudentRepository studentRepository;

    public StudentJoinService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void register(Student student) {
        // 이름+학번+전화번호 조합 중복 확인
        if (studentRepository.existsByNameAndStudentNumberAndEmail(
                student.getName(),
                student.getStudentNumber(),
                student.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 사용자 정보입니다.");
        }

        // 저장
        studentRepository.save(student);
    }

    public boolean isUserIdDuplicate(String userId) {
        return studentRepository.existsBystudentId(userId);
    }

    //로그인 로직
    public boolean login(String userId, String password) {
        Optional<Student> optionalStudent = studentRepository.findBystudentId(userId);

        if (optionalStudent.isEmpty()) {
            return false;
        }

        Student student = optionalStudent.get();

        return student.getPassword().equals(password);
    }

}
