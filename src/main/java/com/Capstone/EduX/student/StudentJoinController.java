package com.Capstone.EduX.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        //ID 확인
        @GetMapping("/check-id")
        public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
            boolean isDuplicate = studentJoinService.isUserIdDuplicate(userId);
            return ResponseEntity.ok(isDuplicate);
        }

    }
}
