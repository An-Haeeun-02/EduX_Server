package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.examInfo.ExamInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

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



}
