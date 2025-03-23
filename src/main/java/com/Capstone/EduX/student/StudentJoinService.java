package com.Capstone.EduX.student;

import org.springframework.stereotype.Service;

@Service
public class StudentJoinService {
    private final StudentRepository studentRepository;

    public StudentJoinService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void register(Student student) {
        // 이름+학번+전화번호 조합 중복 확인
        if (studentRepository.existsByNameAndStudentNumberAndPhoneNumber(
                student.getName(),
                student.getStudentNumber(),
                student.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 사용자 정보입니다.");
        }

        // 저장
        studentRepository.save(student);
    }

    public boolean isUserIdDuplicate(String userId) {
        return studentRepository.existsByUserId(userId);
    }

}
