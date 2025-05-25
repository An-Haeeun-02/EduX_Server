package com.Capstone.EduX.examRange;

import com.Capstone.EduX.examInfo.ExamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRangeRepository extends JpaRepository<ExamRange, Long> {
    void deleteByExamInfo(ExamInfo examInfo);
    void deleteByExamInfo_Id(Long examId);

    List<ExamRange> findByExamInfo(ExamInfo examInfo);
}
