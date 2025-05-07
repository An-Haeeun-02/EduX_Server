// Log.java
package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.examInfo.ExamInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StudentClassroom studentClassroom; //학생 목록

    @ManyToOne
    private Classroom classroom; //강의실

    @ManyToOne
    private ExamInfo examInfo; //시험 정보

    private String logType;

    private String timestamp; // 행동 발생 시간

    private String detail; // 추가 설명 (ex: "시험 점수: 90점", "출석 완료")

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentClassroom getStudentClassroom() {
        return studentClassroom;
    }

    public void setStudentClassroom(StudentClassroom studentClassroom) {
        this.studentClassroom = studentClassroom;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.examInfo = examInfo;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}