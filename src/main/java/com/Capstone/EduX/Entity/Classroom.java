package com.Capstone.EduX.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "classroom")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;
    private String accessCode;

    @ManyToOne
    private Professor professor;
}