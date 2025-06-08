package com.Capstone.EduX.student;

import com.Capstone.EduX.LoginSession.LoginSession;
import com.Capstone.EduX.LoginSession.LoginSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentJoinService {
    private final StudentRepository studentRepository;
    private final LoginSessionRepository loginSessionRepository;

    public StudentJoinService(StudentRepository studentRepository, LoginSessionRepository loginSessionRepository) {
        this.studentRepository = studentRepository;
        this.loginSessionRepository = loginSessionRepository;
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
    public String login(String userId, String password, String sessionId) {
        Optional<Student> optionalStudent = studentRepository.findBystudentId(userId);
        if (optionalStudent.isEmpty()) {
            return "NO_USER";
        }

        Student student = optionalStudent.get();
        if (!student.getPassword().equals(password)) {
            return "WRONG_PASSWORD";
        }

        if (loginSessionRepository.findByStudent(student).isPresent()) {
            return "ALREADY_LOGGED_IN";
        }

        // 로그인 성공
        LoginSession loginSession = new LoginSession();
        loginSession.setSessionId(sessionId);
        loginSession.setStudent(student);
        loginSession.setLoginTime(LocalDateTime.now());
        loginSessionRepository.save(loginSession);

        return "SUCCESS";
    }




}
