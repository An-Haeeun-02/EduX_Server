package com.Capstone.EduX.score;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.examInfo = examInfo;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getStudentTestStartTime() {
        return studentTestStartTime;
    }

    public void setStudentTestStartTime(LocalDateTime studentTestStartTime) {
        this.studentTestStartTime = studentTestStartTime;
    }

    public LocalDateTime getStudentTestEndTime() {
        return studentTestEndTime;
    }

    public void setStudentTestEndTime(LocalDateTime studentTestEndTime) {
        this.studentTestEndTime = studentTestEndTime;
    }
}