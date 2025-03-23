package com.Capstone.EduX.Entity;

import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private Student student;

    @ManyToOne
    private ExamInfo examInfo;

    private Integer score;
    private Integer duration;
}