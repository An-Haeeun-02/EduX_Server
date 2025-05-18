package com.Capstone.EduX.professor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    private final ProfessorService professorService;
    private final ProfessorRepository professorRepository;

    public ProfessorController(ProfessorService professorService, ProfessorRepository professorRepository) {
        this.professorService = professorService;
        this.professorRepository = professorRepository;
    }

    // 교수 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Professor professor) {
        try {
            professorService.register(professor);
            return ResponseEntity.ok("교수 회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 아이디 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicate = professorService.isUsernameDuplicate(username);
        return ResponseEntity.ok(isDuplicate);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        boolean success = professorService.login(username, password);

        if (success) {
            // 기존 세션 무효화 후 새 세션 생성
            request.getSession().invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("professorUsername", username);
            session.setMaxInactiveInterval(1800); // 30분 유지
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 로그인 여부 확인
    @GetMapping("/login-check")
    public ResponseEntity<String> loginCheck(@SessionAttribute(name = "professorUsername", required = false) String professorUsername) {
        if (professorUsername == null) {
            return ResponseEntity.status(401).body("로그인하지 않음");
        }
        return ResponseEntity.ok("로그인된 교수 아이디: " + professorUsername);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/find-id")
    public ResponseEntity<?> findId(@RequestParam String name, @RequestParam String email) {
        Optional<Professor> professorOpt = professorRepository.findByNameAndEmail(name, email);
        if (professorOpt.isPresent()) {
            return ResponseEntity.ok(professorOpt.get().getUsername()); // 아이디 반환
        } else {
            return ResponseEntity.status(404).body("일치하는 계정이 없습니다.");
        }
    }

    @GetMapping("/verify-password-reset")
    public ResponseEntity<?> verifyPasswordReset(
            @RequestParam String username,
            @RequestParam String email) {

        boolean exists = professorRepository.existsByUsernameAndEmail(username, email);

        if (exists) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(404).body("일치하는 정보가 없습니다.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String newPassword = payload.get("newPassword");

        Optional<Professor> profOpt = professorRepository.findByUsername(username);
        if (profOpt.isPresent()) {
            Professor prof = profOpt.get();
            prof.setPassword(newPassword); // 실제 서비스에서는 비밀번호 해싱 필요
            professorRepository.save(prof);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(404).body("계정을 찾을 수 없습니다.");
        }
    }


}
