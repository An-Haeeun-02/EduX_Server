package com.Capstone.EduX.gradingResult;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "grading_results")
public class GradingResult {

    @Id
    private String id;

    private String examQuestionId;   // MongoDB의 시험 문제ID
    private Long examResultId;       // MySQL의 시험 결과 PK
    private Integer scorePerQuestion;

    public GradingResult() {}

    public GradingResult(String examQuestionId, Long examResultId, Integer scorePerQuestion) {
        this.examQuestionId = examQuestionId;
        this.examResultId = examResultId;
        this.scorePerQuestion = scorePerQuestion;
    }

    public String getId() {
        return id;
    }

    public String getExamQuestionId() {
        return examQuestionId;
    }

    public void setExamQuestionId(String examQuestionId) {
        this.examQuestionId = examQuestionId;
    }

    public Long getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(Long examResultId) {
        this.examResultId = examResultId;
    }

    public Integer getScorePerQuestion() {
        return scorePerQuestion;
    }

    public void setScorePerQuestion(Integer scorePerQuestion) {
        this.scorePerQuestion = scorePerQuestion;
    }
}