package com.Capstone.EduX.examResult;

import com.Capstone.EduX.examInfo.ExamInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "exam_result")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExamInfo examInfo;

    private Long userId;
    private String examQuestionId;
    private String userAnswer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.examInfo = examInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExamQuestionId() {
        return examQuestionId;
    }

    public void setExamQuestionId(String examQuestionId) {
        this.examQuestionId = examQuestionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}