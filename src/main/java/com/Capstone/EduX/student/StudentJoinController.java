package com.Capstone.EduX.student;

import com.Capstone.EduX.LoginSession.LoginSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
    @RequestMapping("/api/students")
    public class StudentJoinController {

    private final StudentJoinService studentJoinService;
    private final StudentRepository studentRepository;
    private final LoginSessionRepository loginSessionRepository;

    public StudentJoinController(StudentJoinService studentJoinService, StudentRepository studentRepository, LoginSessionRepository loginSessionRepository) {
        this.studentJoinService = studentJoinService;
        this.studentRepository = studentRepository;
        this.loginSessionRepository = loginSessionRepository;
    }

    //로그인 여부
    @GetMapping("/login-check")
    public ResponseEntity<String> loginCheck(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ResponseEntity.status(401).body("로그인하지 않음");
        } else {
            return ResponseEntity.ok("로그인된 사용자 ID: " + studentId);
        }
    }


    //회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Student student) {
        try {
            studentJoinService.register(student);
            return ResponseEntity.ok("학생 회원가입이 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //ID 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String studentId) {
        boolean isDuplicate = studentJoinService.isUserIdDuplicate(studentId);
        return ResponseEntity.ok(isDuplicate);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String studentId = loginData.get("studentId");
        String password = loginData.get("password");

        // 기존 세션 무효화 후 새로 생성
        request.getSession().invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute("studentId", studentId);
        session.setMaxInactiveInterval(1800); // 30분 유지

        String sessionId = session.getId();

        boolean success = studentJoinService.login(studentId, password, sessionId);

        if (success) {
            Student student = studentRepository.findByStudentId(studentId);
            Map<String, Object> result = new HashMap<>();
            result.put("id", student.getId());
            result.put("studentId", student.getStudentId());
            result.put("name", student.getName());
            return ResponseEntity.ok(result);
        } else {
            session.invalidate(); // 실패했으면 세션 다시 무효화
            return ResponseEntity.status(401).body(Map.of("error", "로그인 실패 또는 중복 로그인"));
        }
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        String studentId = (String) session.getAttribute("studentId");
        String sessionId = session.getId();

        if (studentId != null) {
            Student student = studentRepository.findByStudentId(studentId);
            if (student != null) {
                studentRepository.save(student);
            }
        }

        loginSessionRepository.deleteById(sessionId); // 더 간단히 삭제 가능
        session.invalidate();

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }



    //학생 정보 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentInfo(@PathVariable Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id); // ✅ PK로 조회

        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(404).body("학생을 찾을 수 없습니다.");
        }

        Student student = optionalStudent.get();

        Map<String, Object> result = new HashMap<>();
        result.put("studentNumber", student.getStudentNumber());
        result.put("name", student.getName());
        result.put("phoneNumber", student.getEmail()); // 실제 전화번호 아니라 이메일이라면 여긴 그대로

        return ResponseEntity.ok(result);
    }

    // 아이디 찾기
    @GetMapping("/find-id")
    public ResponseEntity<?> findId(@RequestParam String name, @RequestParam String email) {
        Optional<Student> studentOpt = studentRepository.findByNameAndEmail(name, email);
        if (studentOpt.isPresent()) {
            return ResponseEntity.ok(studentOpt.get().getStudentId());
        } else {
            return ResponseEntity.status(404).body("일치하는 계정이 없습니다.");
        }
    }

    // 비밀번호 재설정 전 본인 확인
    @GetMapping("/verify-password-reset")
    public ResponseEntity<?> verifyPasswordReset(@RequestParam String studentId, @RequestParam String email) {
        boolean exists = studentRepository.existsByStudentIdAndEmail(studentId, email);
        if (exists) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(404).body("일치하는 정보가 없습니다.");
        }
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String studentId = payload.get("studentId");
        String newPassword = payload.get("newPassword");

        Student student = studentRepository.findByStudentId(studentId);
        if (student != null) {
            student.setPassword(newPassword); // 실제 운영 환경에서는 비밀번호 해싱이 필요합니다
            studentRepository.save(student);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(404).body("계정을 찾을 수 없습니다.");
        }
    }

}

