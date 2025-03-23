package com.Capstone.EduX.professor;

public interface ProfessorService {
    void register(Professor professor);
    Professor findProfessor(Long id);
    Professor login(String username, String password);
}
