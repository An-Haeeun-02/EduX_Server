package com.Capstone.EduX.professor;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public void register(Professor professor) {
        if (professorRepository.existsByUsername(professor.getUsername())) {
            throw new IllegalArgumentException("이미 등록된 교수 아이디입니다.");
        }
        professorRepository.save(professor);
    }

    public boolean isUsernameDuplicate(String username) {
        return professorRepository.existsByUsername(username);
    }

    public boolean login(String username, String password) {
        Optional<Professor> optionalProfessor = professorRepository.findByUsername(username);
        if (optionalProfessor.isEmpty()) {
            return false;
        }
        Professor professor = optionalProfessor.get();
        return professor.getPassword().equals(password);
    }

    public Professor findByUsername(String username) {
        return professorRepository.findByUsername(username)
                .orElse(null);
    }
}
