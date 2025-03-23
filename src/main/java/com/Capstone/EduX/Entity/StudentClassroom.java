package com.Capstone.EduX.Entity;

import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "student_classroom")
public class StudentClassroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private Student student;

    private boolean isConnected;
}