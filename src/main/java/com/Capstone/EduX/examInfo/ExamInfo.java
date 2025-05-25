package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.Classroom.Classroom;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_info")
public class ExamInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;
    private String notice;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "access_mode", nullable = false)
    private String accessMode = "deny";


    @ManyToOne
    private Classroom classroom;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getTestStartTime() {
        return testStartTime;
    }

    public void setTestStartTime(LocalDateTime testStartTime) {
        this.testStartTime = testStartTime;
    }

    public LocalDateTime getTestEndTime() {
        return testEndTime;
    }

    public void setTestEndTime(LocalDateTime testEndTime) {
        this.testEndTime = testEndTime;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public String getAccessMode() { return accessMode; }
    public void setAccessMode(String accessMode) { this.accessMode = accessMode; }
}