package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoService;
import com.Capstone.EduX.examParticipation.ExamParticipationRepository;
import com.Capstone.EduX.examResult.ExamResultRepository;
import com.Capstone.EduX.gradingResult.GradingResultRepository;
import com.Capstone.EduX.log.LogRepository;
import com.Capstone.EduX.score.ScoreRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentClassroomService {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentClassroomRepository studentClassroomRepository;
    private final ExamInfoService examInfoService;
    private final ExamParticipationRepository examParticipationRepository;
    private final LogRepository logRepository;
    private final ScoreRepository scoreRepository;
    private final GradingResultRepository gradingResultRepository;
    private final ExamResultRepository examResultRepository;

    public StudentClassroomService(StudentRepository studentRepository,
                                   ClassroomRepository classroomRepository,
                                   StudentClassroomRepository studentClassroomRepository,
                                   ExamInfoService examInfoService,
                                   ExamParticipationRepository examParticipationRepository,
                                   LogRepository logRepository,
                                   ScoreRepository scoreRepository,
                                   GradingResultRepository gradingResultRepository,
                                   ExamResultRepository examResultRepository) {
        this.studentRepository = studentRepository;
        this.classroomRepository = classroomRepository;
        this.studentClassroomRepository = studentClassroomRepository;
        this.examInfoService = examInfoService;
        this.examParticipationRepository = examParticipationRepository;
        this.logRepository = logRepository;
        this.scoreRepository = scoreRepository;
        this.gradingResultRepository = gradingResultRepository;
        this.examResultRepository = examResultRepository;
    }

    public List<Classroom> getClassrooms(String studentId) {
        return studentClassroomRepository.findClassroomsByStudentId(studentId);
    }

    public String joinClassroom(String studentId, String accessCode) {
        // 1. 강의실 찾기
        Classroom classroom = classroomRepository.findByAccessCode(accessCode);
        if (classroom == null) {
            throw new IllegalArgumentException("잘못된 코드입니다.");
        }

        // 2. 학생 찾기
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new NoSuchElementException("학생을 찾을 수 없습니다.");
        }

        // 3. 이미 가입했는지 확인
        boolean exists = studentClassroomRepository.existsByStudentAndClassroom(student, classroom);
        if (exists) {
            throw new IllegalStateException("이미 참여한 강의실입니다.");
        }

        // 4. 참여 저장
        StudentClassroom sc = new StudentClassroom();
        sc.setStudent(student);
        sc.setClassroom(classroom);
        sc.setConnected(true);
        studentClassroomRepository.save(sc);

        return classroom.getClassName();
    }
    //강의실 id에 속한 학생 명단 조회
    public List<Student> getStudentsByClassroomId(Long classroomId) {
        return studentClassroomRepository.findStudentsByClassroomId(classroomId);
    }

    // 시험에만 속한 학생 명단 조회 (시험 참여 데이터 사용)
    public List<Student> getStudentsByExamId(Long examId) {
        // 시험 정보가 있는지 확인
        ExamInfo exam = examInfoService.getExamInfoById(examId);
        if (exam == null) {
            throw new NoSuchElementException("시험 정보를 찾을 수 없습니다.");
        }
        // 시험에 참여한 학생만 조회함.
        return examParticipationRepository.findStudentsByExamId(examId);
    }

    // 강의실에서 학생 제거(강퇴)
    @Transactional
    public void removeStudentFromClassroom(Long studentId, Long classroomId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("학생을 찾을 수 없습니다."));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NoSuchElementException("강의실을 찾을 수 없습니다."));
        StudentClassroom relation = studentClassroomRepository.findByStudentAndClassroom(student, classroom);
        if (relation == null) {
            throw new NoSuchElementException("해당 학생은 이 강의실에 등록되어 있지 않습니다.");
        }

        Long studentClassroomId = relation.getId();

        // ✅ 1. 로그 먼저 삭제
        logRepository.deleteByStudentClassroomId(studentClassroomId);

        // ✅ 2. 시험 리스트 가져오기
        List<ExamInfo> exams = examInfoService.getExamsByClassroomId(classroomId);

        for (ExamInfo exam : exams) {
            Long examId = exam.getId();

            // 시험 참여 삭제
            examParticipationRepository.deleteByStudentIdAndExamId(studentId, examId);

            // 점수 삭제
            scoreRepository.deleteByStudentIdAndExamId(studentId, examId);

            // grading_result 삭제 (Mongo)
            gradingResultRepository.deleteByStudentIdAndExamId(studentId, examId);

            // exam_result 삭제 (Mongo)
            examResultRepository.deleteByStudentIdAndExamId(studentId, examId);
        }

        // ✅ 3. student_classroom 삭제
        studentClassroomRepository.delete(relation);
    }



    public List<Map<String, Object>> getStudentsWithExamInfoByClassroomId(Long classroomId) {
        List<Object[]> results = studentClassroomRepository.findStudentsWithExamInfoByClassroomId(classroomId);

        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", row[0]);
            map.put("name", row[1]);
            map.put("studentTestStartTime", row[2]);
            map.put("studentTestEndTime", row[3]);
            map.put("score", row[4]);
            return map;
        }).collect(Collectors.toList());
    }

}
