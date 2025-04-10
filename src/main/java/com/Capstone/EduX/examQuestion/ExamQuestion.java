package com.Capstone.EduX.examQuestion;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

//@Entity
//@Table(name = "exam_question")
@Document(collection = "exam_questions")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private String id;
    private Long examId;
    private Integer number;
    private String question;
    private List<String> distractor;
    private String answer;
    private String type;
    private Integer questionScore;

    public ExamQuestion(Long examId, Integer number, String question,
                        List<String> distractor, String answer, String type, Integer questionScore) {
        this.examId = examId;
        this.number = number;
        this.question = question;
        this.distractor = distractor;
        this.answer = answer;
        this.type = type;
        this.questionScore = questionScore;
    }

    public String getId() {
        return id;
    }

    public List<String> getDistractor() {
        return distractor;
    }

    public void setDistractor(List<String> distractor) {
        this.distractor = distractor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(Integer questionScore) {
        this.questionScore = questionScore;
    }
}

