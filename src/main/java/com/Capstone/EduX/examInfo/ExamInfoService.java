package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ExamInfoService {

    private final ExamInfoRepository examInfoRepository;
    private final ClassroomRepository classroomRepository;

    public ExamInfoService(ExamInfoRepository examInfoRepository,ClassroomRepository classroomRepository) {
        this.examInfoRepository = examInfoRepository;
        this.classroomRepository = classroomRepository;
    }

//    public List<ExamInfo> getActiveExams(Long classroomId) {
//        return repository.findActiveExamsByClassroomId(classroomId, LocalDateTime.now());
//    }

    public ExamInfo createExam(ExamCreateRequest request) {
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("강의실을 찾을 수 없습니다."));

        ExamInfo exam = new ExamInfo();
        exam.setTitle(request.getTitle());
        exam.setClassroom(classroom);

        return examInfoRepository.save(exam);
    }

    public List<ExamInfo> getExamsByClassroomIdAndProfessorId(Long classroomId, Long professorId) {
        return examInfoRepository.findAllExamsByClassroomIdAndProfessorId(classroomId, professorId);
    }

    public String updateExamInfo(Map<String, Object> req) {
        Long examId = Long.valueOf(req.get("id").toString());

        ExamInfo exam = examInfoRepository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("해당 시험이 존재하지 않습니다."));

        // 수정 가능한 필드들 업데이트
        exam.setTitle((String) req.get("title"));
        exam.setNotice((String) req.get("notice"));
        exam.setQuestionCount((Integer) req.get("questionCount"));

        exam.setStartTime(LocalDateTime.parse((String) req.get("startTime")));
        exam.setEndTime(LocalDateTime.parse((String) req.get("endTime")));
        exam.setTestStartTime(LocalDateTime.parse((String) req.get("testStartTime")));
        exam.setTestEndTime(LocalDateTime.parse((String) req.get("testEndTime")));

        examInfoRepository.save(exam);

        return exam.getTitle();
    }

    public ExamInfo getExamInfoById(Long examId) {
        return examInfoRepository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("시험 정보를 찾을 수 없습니다."));

    }

    //시험 대기 접근 활성화 여부
    public Map<String, Object> checkIdleExamAccess(Long examId) {
        //시험 존재 여부 조회
        ExamInfo exam = examInfoRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시험이 존재하지 않습니다."));

        //현재 시간 now변수에 저장
        LocalDateTime now = LocalDateTime.now();

        //현재 시간을 기준으로 비교
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new IllegalStateException("접근 가능 시간이 아닙니다."); //예외 발생
        }

        // 응답 데이터만 뽑아서 Map으로 구성
        Map<String, Object> response = new HashMap<>();
        response.put("title", exam.getTitle());
        response.put("notice", exam.getNotice());
        response.put("testStartTime", exam.getTestStartTime());

        return response;
    }

    //시험 대기 접근 활성화 여부
    public Map<String, Object> checkExamAccess(Long examId) {
        //시험 존재 여부 조회
        ExamInfo exam = examInfoRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시험이 존재하지 않습니다."));

        //현재 시간 now변수에 저장
        LocalDateTime now = LocalDateTime.now();

        //현재 시간을 기준으로 비교
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new IllegalStateException("접근 가능 시간이 아닙니다."); //예외 발생
        }

        Duration duration = Duration.between(exam.getStartTime(), exam.getEndTime());

        // 응답 데이터만 뽑아서 Map으로 구성
        Map<String, Object> response = new HashMap<>();
        response.put("title", exam.getTitle());
        response.put("id", exam.getId());
        response.put("testStartTime", exam.getTestStartTime());
        response.put("testEndTime", exam.getEndTime());
        response.put("testTime", duration.toMinutes()); //분으로 출력

        return response;
    }


}
