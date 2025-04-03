package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.student.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "student_classroom")
public class StudentClassroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Classroom classroomID;

    @ManyToOne
    private Student student;

    private boolean isConnected;

    public StudentClassroom(Long id, Classroom classroom, Student student, boolean isConnected) {
        this.id = id;
        this.classroomID = classroom;
        this.student = student;
        this.isConnected = isConnected;
    }

    public StudentClassroom() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroomID;
    }

    public void setClassroom(Classroom classroom) {
        this.classroomID = classroom;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}