package com.Capstone.EduX.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_question")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Long examId;
    private String question;
    private String answer;
    private String type;
    private float questionScore;
}