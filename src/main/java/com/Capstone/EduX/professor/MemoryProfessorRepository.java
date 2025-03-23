package com.Capstone.EduX.professor;

import java.util.HashMap;
import java.util.Map;

public class MemoryProfessorRepository implements ProfessorRepository {

    private static Map<Long, Professor> store = new HashMap<>();

    @Override
    public void save(Professor professor) {
        store.put(professor.getId(), professor);
    }

    @Override
    public Professor findById(Long id) {
        return store.get(id);
    }

    @Override
    public Professor findByUsername(String username) {
        return store.values().stream()
                .filter(professor -> professor.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public Map<Long, Professor> findAll() {
        return store;
    }
}
