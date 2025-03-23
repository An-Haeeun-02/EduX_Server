// Log.java
package com.Capstone.EduX.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StudentClassroom studentClassroom;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private ExamInfo examInfo;

    private String logType;
}