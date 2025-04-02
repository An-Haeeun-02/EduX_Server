package com.Capstone.EduX.student;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentJoinController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentJoinService studentJoinService;

    @Test
    @DisplayName("로그인 성공 시 세션 생성")
    void loginSuccess() throws Exception {
        // given
        String studentId = "test123";
        String password = "pass123";

        when(studentJoinService.login(studentId, password)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/api/students/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "studentId": "test123",
                      "password": "pass123"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"));
    }

    @Test
    @DisplayName("로그인 실패 시 401 반환")
    void loginFail() throws Exception {
        when(studentJoinService.login("wrong", "wrongpass")).thenReturn(false);

        mockMvc.perform(post("/api/students/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "studentId": "wrong",
                      "password": "wrongpass"
                    }
                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("아이디 또는 비밀번호가 잘못되었습니다."));
    }

    @Test
    @DisplayName("로그인 상태 확인 (세션 있음)")
    void loginCheck_withSession() throws Exception {
        mockMvc.perform(get("/api/students/login-check")
                        .sessionAttr("studentId", "test123")) // 세션에 사용자 정보 넣음
                .andExpect(status().isOk())
                .andExpect(content().string("로그인된 사용자 ID: test123"));
    }

    @Test
    @DisplayName("로그인 상태 확인 (세션 없음)")
    void loginCheck_noSession() throws Exception {
        mockMvc.perform(get("/api/students/login-check"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("로그인하지 않음"));
    }
}
