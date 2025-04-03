package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamInfoService {

    private final ExamInfoRepository repository;
    private final ClassroomRepository classroomRepository;

    public ExamInfoService(ExamInfoRepository repository,ClassroomRepository classroomRepository) {
        this.repository = repository;
        this.classroomRepository = classroomRepository;
    }

    public List<ExamInfo> getActiveExams(Long classroomId) {
        return repository.findActiveExamsByClassroomId(classroomId, LocalDateTime.now());
    }

    public ExamInfo createExam(ExamCreateRequest request) {
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("강의실을 찾을 수 없습니다."));

        ExamInfo exam = new ExamInfo();
        exam.setTitle(request.getTitle());
        exam.setTimeLimit(request.getTimeLimit());
        exam.setDuration(request.getDuration());
        exam.setNotice(request.getNotice());
        exam.setQuestionCount(request.getQuestionCount());
        exam.setClassroom(classroom);

        return repository.save(exam);
    }

    public List<ExamInfo> getExamsByClassroomIdAndProfessorId(Long classroomId, Long professorId) {
        return repository.findAllExamsByClassroomIdAndProfessorId(classroomId, professorId);
    }


}
