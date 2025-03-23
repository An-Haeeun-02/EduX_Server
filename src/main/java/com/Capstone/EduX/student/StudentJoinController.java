package com.Capstone.EduX.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public class StudentJoinController {

    @RestController
    @RequestMapping("/api/students")
    public class StudentController {

        private final StudentJoinService studentJoinService;

        public StudentController(StudentJoinService studentJoinService) {
            this.studentJoinService = studentJoinService;
        }

        //회원가입
        @PostMapping("/register")
        public ResponseEntity<String> register(@RequestBody Student student) {
            try {
                studentJoinService.register(student);
                return ResponseEntity.ok("회원가입이 완료되었습니다.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        //ID 중복 확인
        @GetMapping("/check-id")
        public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
            boolean isDuplicate = studentJoinService.isUserIdDuplicate(userId);
            return ResponseEntity.ok(isDuplicate);
        }

        @PostMapping("/login")
        public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
            String userId = loginData.get("userId");
            String password = loginData.get("password");

            boolean success = studentJoinService.login(userId, password);

            if (success) {
                return ResponseEntity.ok("로그인 성공");
            } else {
                return ResponseEntity.status(401).body("아이디 또는 비밀번호가 잘못되었습니다.");
            }
        }

    }
}
