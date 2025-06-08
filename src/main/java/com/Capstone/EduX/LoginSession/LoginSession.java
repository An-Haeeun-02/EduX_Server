package com.Capstone.EduX.LoginSession;

import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LoginSession")
public class LoginSession {

    @Id
    private String sessionId; // 세션 ID 자체를 PK로 사용

    @OneToOne
    private Student student; // 로그인한 학생

    private LocalDateTime loginTime;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}
