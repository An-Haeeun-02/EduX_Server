package com.Capstone.EduX.professor;

public interface ProfessorRepository {
    void save(Professor professor);
    Professor findById(Long id);
    Professor findByUsername(String username);
}
