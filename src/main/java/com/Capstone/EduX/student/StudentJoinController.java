package com.Capstone.EduX.student;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;


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
        public ResponseEntity<String> login(@RequestBody Map<String, String> loginData, HttpServletRequest httpServletRequest) {
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
                return ResponseEntity.ok("로그인 성공");
            } else {
                return ResponseEntity.status(401).body("아이디 또는 비밀번호가 잘못되었습니다.");
            }
        }

        //로그아웃
        @PostMapping("/logout")
        public ResponseEntity<String> logout(HttpSession session) {
            session.invalidate(); // ← 현재 로그인한 사용자의 세션 무효화 (로그아웃 처리)
            return ResponseEntity.ok("로그아웃 되었습니다.");
        }

        //학생 정보 가져오기
        @GetMapping("/{studentId}")
        public ResponseEntity<?> getStudentInfo(@PathVariable String studentId) {
            Student student = studentRepository.findByStudentId(studentId);

            if (student == null) {
                return ResponseEntity.status(404).body("학생을 찾을 수 없습니다.");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("studentNumber", student.getStudentNumber());
            result.put("name", student.getName());
            result.put("phoneNumber", student.getPhoneNumber());

            return ResponseEntity.ok(result);
        }





    }

