package com.Capstone.EduX.Entity;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    private LocalDateTime studentTestStartTime;
    private LocalDateTime studentTestEndTime;
}