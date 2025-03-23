package com.Capstone.EduX.Entity;

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
}