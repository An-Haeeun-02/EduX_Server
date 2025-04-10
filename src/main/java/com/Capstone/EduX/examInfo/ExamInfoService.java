package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
        exam.setClassroom(classroom);

        return repository.save(exam);
    }

    public List<ExamInfo> getExamsByClassroomIdAndProfessorId(Long classroomId, Long professorId) {
        return repository.findAllExamsByClassroomIdAndProfessorId(classroomId, professorId);
    }

    public String updateExamInfo(Map<String, Object> req) {
        Long examId = Long.valueOf(req.get("id").toString());

        ExamInfo exam = repository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("해당 시험이 존재하지 않습니다."));

        // 수정 가능한 필드들 업데이트
        exam.setTitle((String) req.get("title"));
        exam.setNotice((String) req.get("notice"));
        exam.setQuestionCount((Integer) req.get("questionCount"));

        exam.setStartTime(LocalDateTime.parse((String) req.get("startTime")));
        exam.setEndTime(LocalDateTime.parse((String) req.get("endTime")));
        exam.setTestStartTime(LocalDateTime.parse((String) req.get("testStartTime")));
        exam.setTestEndTime(LocalDateTime.parse((String) req.get("testEndTime")));

        repository.save(exam);

        return exam.getTitle();
    }


}
