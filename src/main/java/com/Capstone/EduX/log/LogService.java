package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroomRepository;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LogService {

    private final StudentRepository studentRepository;
    private final StudentClassroomRepository studentClassroomRepository;
    private final ClassroomRepository classroomRepository;
    private final ExamInfoRepository examInfoRepository;
    private final LogRepository logRepository;

    public void saveStudentAction(StudentClassroom studentClassroom, Classroom classroom, ExamInfo examInfo,
                                  String logType, String detail) {
        Log log = new Log();
        log.setStudentClassroom(studentClassroom);
        log.setClassroom(classroom);
        log.setExamInfo(examInfo);
        log.setLogType(logType);
        log.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.setDetail(detail);

        logRepository.save(log);
    }

    public List<Log> getLogsByClassroomId(Long classroomId) {
        return logRepository.findByClassroomId(classroomId);
    }

    public List<Log> getLogsByStudentClassroomId(Long studentClassroomId) {
        return logRepository.findByStudentClassroomId(studentClassroomId);
    }

    public List<Log> getLogsByExamInfoId(Long examInfoId) {
        return logRepository.findByExamInfoId(examInfoId);
    }

    public List<Log> getLogsByLogType(String logType) {
        return logRepository.findByLogType(logType);
    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public void saveLog(String studentId,
                        LocalDateTime timestamp,
                        LogType logType,
                        Long classroomId,
                        Long examInfoId,
                        String detail) {

        // 1. 학생 조회
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new NotFoundException("학생 ID '" + studentId + "'를 찾을 수 없습니다."));


        // 2. 로그 엔티티 생성
        Log log = new Log();
        log.setLogType(logType.name());
        log.setTimestamp(timestamp.toString());
        log.setDetail(detail);

        // 3. 필요한 엔티티 세팅
        if (classroomId != null) {
            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new NotFoundException("강의실 ID '" + classroomId + "'를 찾을 수 없습니다."));

            StudentClassroom studentClassroom = studentClassroomRepository
                    .findByStudentIdAndClassroomId(student.getId(), classroomId)
                    .orElseThrow(() -> new NotFoundException("학생이 해당 강의실에 등록되어 있지 않습니다."));

            log.setClassroom(classroom);
            log.setStudentClassroom(studentClassroom);
        }

        if (examInfoId != null) {
            ExamInfo examInfo = examInfoRepository.findById(examInfoId)
                    .orElseThrow(() -> new NotFoundException("시험 ID '" + examInfoId + "'를 찾을 수 없습니다."));
            log.setExamInfo(examInfo);
        }

        logRepository.save(log);
    }


}
