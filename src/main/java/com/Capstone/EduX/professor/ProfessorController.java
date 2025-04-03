package com.Capstone.EduX.professor;

import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpSession; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/professors") public class ProfessorController {

    private final ProfessorService professorService;
    private final ProfessorRepository professorRepository;

    public ProfessorController(ProfessorService professorService, ProfessorRepository professorRepository) {
        this.professorService = professorService;
        this.professorRepository = professorRepository;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Professor professor) {
        // 중복 아이디 확인을 여기서 처리할 수도 있음
        if (professorRepository.findByUsername(professor.getUsername()) != null) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }
        professorService.register(professor);
        return ResponseEntity.ok("교수 회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Professor professor = professorService.login(username, password);
        if (professor != null) {
            // 기존 세션 무효화 후 새 세션 생성
            request.getSession().invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("professorId", professor.getId());
            session.setMaxInactiveInterval(1800); // 30분 동안 유지
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    // 중복 ID 확인
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = professorRepository.findByUsername(username) != null;
        return ResponseEntity.ok(exists);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 현재 세션 무효화 (로그아웃)
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}