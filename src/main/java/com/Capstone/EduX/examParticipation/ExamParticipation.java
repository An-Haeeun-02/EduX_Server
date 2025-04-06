package com.Capstone.EduX.examParticipation;

import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "exam_participation")
public class ExamParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExamInfo exam;

    @ManyToOne
    private Student student;

    public ExamParticipation() {
    }

    public ExamParticipation(ExamInfo exam, Student student) {
        this.exam = exam;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public ExamInfo getExam() {
        return exam;
    }

    public void setExam(ExamInfo exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}