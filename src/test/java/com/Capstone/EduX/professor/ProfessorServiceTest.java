package com.Capstone.EduX.professor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfessorServiceTest {

    ProfessorService professorService;

    @BeforeEach
    void setUp() {
        ProfessorRepository professorRepository = new MemoryProfessorRepository();
        professorService = new ProfessorServiceImpl(professorRepository);
    }

    @Test
    void 교수_회원가입_성공() {
        // given
        Professor professor = new Professor(
                1L,
                "김교수",
                "kim123",
                "password123",
                "kim@univ.ac.kr",
                "컴퓨터공학과"
        );

        // when
        professorService.register(professor);
        Professor found = professorService.findProfessor(1L);

        // then
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getUsername()).isEqualTo("kim123");
        Assertions.assertThat(found.getDepartment()).isEqualTo("컴퓨터공학과");
    }

    @Test
    void 교수_로그인_성공() {
        // given
        Professor professor = new Professor(
                2L,
                "이교수",
                "lee456",
                "pass456",
                "lee@univ.ac.kr",
                "전자공학과"
        );
        professorService.register(professor);

        // when
        Professor loggedIn = professorService.login("lee456", "pass456");

        // then
        Assertions.assertThat(loggedIn).isNotNull();
        Assertions.assertThat(loggedIn.getName()).isEqualTo("이교수");
    }

    @Test
    void 교수_로그인_실패_잘못된_비밀번호() {
        // given
        Professor professor = new Professor(
                3L,
                "박교수",
                "park789",
                "securepass",
                "park@univ.ac.kr",
                "기계공학과"
        );
        professorService.register(professor);

        // when
        Professor loggedIn = professorService.login("park789", "wrongpass");

        // then
        Assertions.assertThat(loggedIn).isNull();
    }

    @Test
    void 교수_로그인_실패_존재하지_않는_아이디() {
        // when
        Professor loggedIn = professorService.login("noone", "nopass");

        // then
        Assertions.assertThat(loggedIn).isNull();
    }
}