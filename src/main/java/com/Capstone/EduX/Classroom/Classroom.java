package com.Capstone.EduX.Classroom;

import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.professor.Professor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classroom")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;
    private String section;
    private String time;
    private String accessCode;

    @ManyToOne
    private Professor professor;

    @OneToMany(
            mappedBy = "classroom",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StudentClassroom> studentClassrooms = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}