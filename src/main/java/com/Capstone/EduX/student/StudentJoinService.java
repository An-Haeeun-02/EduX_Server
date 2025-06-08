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

        // ⭐ active 상태 false로 초기화
        student.setActive(false);

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

        if (student.getPassword().equals(password)) {
            // ⭐ 로그인 성공 시 active를 true로 설정하고 저장
            student.setActive(true);
            studentRepository.save(student);
            return true;
        }

        return student.getPassword().equals(password);
    }

}
