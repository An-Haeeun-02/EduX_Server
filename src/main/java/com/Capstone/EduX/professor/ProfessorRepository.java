package com.Capstone.EduX.professor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByUsername(String username);
    Optional<Professor> findByUsername(String username);
    Optional<Professor> findByNameAndEmail(String name, String email);
    boolean existsByUsernameAndEmail(String username, String email);
}
