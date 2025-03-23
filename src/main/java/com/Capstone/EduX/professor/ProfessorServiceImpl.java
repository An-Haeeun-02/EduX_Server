package com.Capstone.EduX.professor;

public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public void register(Professor professor) {
        professorRepository.save(professor);
    }

    @Override
    public Professor findProfessor(Long id) {
        return professorRepository.findById(id);
    }

    @Override
    public Professor login(String username, String password) {
        // 1. 아이디로 교수 찾기
        Professor professor = professorRepository.findByUsername(username);

        // 2. 비밀번호 일치 여부 확인
        if (professor != null && professor.getPassword().equals(password)) {
            return professor; // 로그인 성공
        }

        return null; // 로그인 실패
    }
}
