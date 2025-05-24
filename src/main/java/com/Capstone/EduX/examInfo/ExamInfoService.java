package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroomRepository;
import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import com.Capstone.EduX.examQuestion.ExamQuestionRepository;
import com.Capstone.EduX.examRange.ExamRangeRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamInfoService {

    private final ExamInfoRepository examInfoRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final ExamRangeRepository examRangeRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final StudentClassroomRepository studentClassroomRepository;

    public ExamInfoService(StudentRepository studentRepository, ExamInfoRepository examInfoRepository, ClassroomRepository classroomRepository, ExamRangeRepository examRangeRepository, ExamQuestionRepository examQuestionRepository, StudentClassroomRepository studentClassroomRepository) {
        this.studentRepository = studentRepository;
        this.examInfoRepository = examInfoRepository;
        this.classroomRepository = classroomRepository;
        this.examRangeRepository = examRangeRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.studentClassroomRepository = studentClassroomRepository;
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
        // 제목
        if (req.containsKey("title") && req.get("title") != null) {
            exam.setTitle((String) req.get("title"));
        }

        // 공지
        if (req.containsKey("notice") && req.get("notice") != null) {
            exam.setNotice((String) req.get("notice"));
        }

        // 문항 수
        if (req.containsKey("questionCount") && req.get("questionCount") != null) {
            Object rawCount = req.get("questionCount");
            if (rawCount instanceof Number) {
                exam.setQuestionCount(((Number) rawCount).intValue());
            } else {
                exam.setQuestionCount(Integer.parseInt(rawCount.toString()));
            }
        }

        // 시작 시간
        if (req.containsKey("startTime") && req.get("startTime") != null && !req.get("startTime").toString().isBlank()) {
            exam.setStartTime(LocalDateTime.parse(req.get("startTime").toString()));
        }

        // 종료 시간
        if (req.containsKey("endTime") && req.get("endTime") != null && !req.get("endTime").toString().isBlank()) {
            exam.setEndTime(LocalDateTime.parse(req.get("endTime").toString()));
        }

        // 시험 시작 시간
        if (req.containsKey("testStartTime") && req.get("testStartTime") != null && !req.get("testStartTime").toString().isBlank()) {
            exam.setTestStartTime(LocalDateTime.parse(req.get("testStartTime").toString()));
        }

        // 시험 종료 시간
        if (req.containsKey("testEndTime") && req.get("testEndTime") != null && !req.get("testEndTime").toString().isBlank()) {
            exam.setTestEndTime(LocalDateTime.parse(req.get("testEndTime").toString()));
        }
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

    public List<Map<String, Object>> getExamTitles(String studentId, Long classroomId) {
        // 1. 학번으로 Student 조회
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new NoSuchElementException("학생과 강의실을 찾을 수 없습니다.");
        }

        Long studentPK = student.getId(); // 내부 식별자

        // 2. student_id와 classroom_id로 StudentClassroom 확인
        Optional<StudentClassroom> optional = studentClassroomRepository
                .findByStudentIdAndClassroomId(studentPK, classroomId);

        if (optional.isEmpty()) {
            throw new IllegalArgumentException("강의실에 없는 학생입니다");
        }

        // 3. classroomId로 시험 정보 조회
        List<ExamInfo> examList = examInfoRepository.findByClassroomId(classroomId);


        // 4. id와 title만 추출하여 반환
        return examList.stream()
                .map(exam -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", exam.getId());
                    map.put("title", exam.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public void deleteExam(Long examId) {
        if (!examInfoRepository.existsById(examId)) {
            throw new NoSuchElementException("해당 시험이 존재하지 않습니다.");
        }
        examInfoRepository.deleteById(examId);
    }

    @Transactional
    public void deleteExamCascade(Long examId) {
        // 1. 시험 범위 삭제 (MySQL)
        try {
            examRangeRepository.deleteByExamInfo_Id(examId);
            System.out.println("시험 범위 삭제 완료");
        } catch (Exception e) {
            System.out.println("시험 범위 삭제 실패 또는 없음: " + e.getMessage());
        }

        // 2. 시험 문제 삭제 (MongoDB)
        try {
            examQuestionRepository.deleteByExamId(examId);
            System.out.println("시험 문제 삭제 완료");
        } catch (Exception e) {
            System.out.println("시험 문제 삭제 실패 또는 없음: " + e.getMessage());
        }

        // 3. 시험 정보 삭제 (MySQL)
        try {
            if (examInfoRepository.existsById(examId)) {
                examInfoRepository.deleteById(examId);
                System.out.println("시험 정보 삭제 완료");
            } else {
                System.out.println("시험 정보 없음 (삭제 건너뜀)");
            }
        } catch (Exception e) {
            System.out.println("시험 정보 삭제 실패: " + e.getMessage());
        }
    }

    public ExamInfo getExamById(Long id) {
        return examInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("시험을 찾을 수 없습니다: id=" + id));
    }


}
