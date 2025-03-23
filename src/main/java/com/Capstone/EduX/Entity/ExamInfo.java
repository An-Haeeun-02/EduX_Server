package com.Capstone.EduX.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_info")
public class ExamInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer timeLimit;
    private Integer duration;
    private String notice;
    private String subjectName;
    private Integer questionCount;

    @ManyToOne
    private Classroom classroom;
}