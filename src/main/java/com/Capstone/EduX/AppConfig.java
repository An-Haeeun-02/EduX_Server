package com.Capstone.EduX;

import com.Capstone.EduX.professor.MemoryProfessorRepository;
import com.Capstone.EduX.professor.ProfessorRepository;
import com.Capstone.EduX.professor.ProfessorService;
import com.Capstone.EduX.professor.ProfessorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ProfessorService professorService() {
        return new ProfessorServiceImpl(professorRepository());
    }

    @Bean
    public ProfessorRepository professorRepository() {
        return new MemoryProfessorRepository();
    }
}
