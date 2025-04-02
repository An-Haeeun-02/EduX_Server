package com.Capstone.EduX.examInfo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamInfoService {

    private final ExamInfoRepository repository;

    public ExamInfoService(ExamInfoRepository repository) {
        this.repository = repository;
    }

    public List<ExamInfo> getActiveExams(Long classroomId) {
        return repository.findActiveExamsByClassroomId(classroomId, LocalDateTime.now());
    }
}
