// ExamRange.java
package com.Capstone.EduX.Entity;

import com.Capstone.EduX.examInfo.ExamInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "exam_range")
public class ExamRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExamInfo examInfo;

    private String rangeDetail;
}