package com.Capstone.EduX.examRange;

import com.Capstone.EduX.examInfo.ExamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRangeRepository extends JpaRepository<ExamRange, Long> {
    void deleteByExamInfo(ExamInfo examInfo);

}
