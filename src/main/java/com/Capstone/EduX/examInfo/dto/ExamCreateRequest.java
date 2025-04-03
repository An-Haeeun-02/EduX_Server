package com.Capstone.EduX.examInfo.dto;

public class ExamCreateRequest {
    private String title;
    private Long classroomId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }
}
