// ExamRange.java
package com.Capstone.EduX.examRange;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.examInfo = examInfo;
    }

    public String getRangeDetail() {
        return rangeDetail;
    }

    public void setRangeDetail(String rangeDetail) {
        this.rangeDetail = rangeDetail;
    }
}