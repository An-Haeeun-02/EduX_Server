package com.Capstone.EduX.student;

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

    public StudentJoinController(StudentJoinService studentJoinService, StudentRepository studentRepository) {
        this.studentJoinService = studentJoinService;
        this.studentRepository = studentRepository;
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpServletRequest httpServletRequest) {
        String studentId = loginData.get("studentId");
        String password = loginData.get("password");

        boolean success = studentJoinService.login(studentId, password);

        if (success) {
            // 로그인 성공 => 세션 생성

            // 세션을 생성하기 전에 기존의 세션 파기
            httpServletRequest.getSession().invalidate();
            HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성
            // 세션에 studentId를 넣어줌
            session.setAttribute("studentId", studentId);
            session.setMaxInactiveInterval(1800); // Session이 30분동안 유지

            // 학생 정보 응답 추가
            Student student = studentRepository.findByStudentId(studentId);
            Map<String, Object> result = new HashMap<>();
            result.put("id", student.getId());  // ✅ 프론트가 사용할 고유 ID
            result.put("studentId", student.getStudentId());
            result.put("name", student.getName());

            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        String studentId = (String) session.getAttribute("studentId");

        if (studentId != null) {
            Student student = studentRepository.findByStudentId(studentId);
            if (student != null) {
                student.setActive(false); // ⭐ active를 false로 설정
                studentRepository.save(student); // DB에 저장
            }
        }

        session.invalidate(); // 세션 무효화 (로그아웃 처리)
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

