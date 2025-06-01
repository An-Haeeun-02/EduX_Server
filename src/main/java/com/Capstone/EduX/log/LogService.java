package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroomRepository;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.StudentClassroom.StudentClassroomRepository;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogService {

    private final StudentClassroomRepository scRepository;
    private final LogRepository logRepository;
    private final StudentRepository studentRepository;
    private final ExamInfoRepository examInfoRepository;
    private final StudentClassroomRepository studentClassroomRepository;
    private final ClassroomRepository classroomRepository;

    private static final DateTimeFormatter PARSER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new NoSuchElementException("학생 ID '" + studentId + "'를 찾을 수 없습니다.");
        }



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

    /**
     * 교수 전용: 학번(studentNumber) + 강의실ID(classroomId) 로
     * 해당 학생의 로그를 DTO 리스트로 반환
     */
    public List<LogDto> getLogsForProfessor(Long studentNumber, Long classroomId) {
        StudentClassroom sc = scRepository
                .findByStudent_StudentNumberAndClassroom_Id(studentNumber, classroomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 학번의 학생이 이 강의실에 없습니다."
                ));

        return logRepository.findByStudentClassroomId(sc.getId()).stream()
                .map(log -> new LogDto(
                        log.getTimestamp(),
                        log.getLogType(),
                        log.getDetail()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 교수용 "미응시"   : 해당 시험에 입장한 기록이 전혀 없을 때
     *         "응시완료": 입장 후 제출 기록이 있을 때
     *         "응시중" : 입장 기록만 있고, 아직 제출은 없을 때
     */
    public String getExamStatus(Long studentNumber, Long classroomId, Long examInfoId) {
        // 1) studentClassroom 조회
        StudentClassroom sc = scRepository
                .findByStudent_StudentNumberAndClassroom_Id(studentNumber, classroomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 학번의 학생이 이 강의실에 없습니다."
                ));

        // 2) 해당 학생·해당 시험 로그 조회
        List<Log> logs = logRepository
                .findByStudentClassroom_IdAndExamInfo_Id(sc.getId(), examInfoId);

        // 3) 판별: DB에는 IN_EXAM, SUBMIT_EXAM 으로 들어가 있으므로, 무조건 실제 값과 매칭
        boolean hasEntry  = logs.stream()
                .anyMatch(l -> "IN_EXAM".equalsIgnoreCase(l.getLogType()));
        boolean hasSubmit = logs.stream()
                .anyMatch(l -> "SUBMIT_EXAM".equalsIgnoreCase(l.getLogType()));

        if (!hasEntry)    return "미응시";
        if (hasSubmit)    return "응시완료";
        return "응시중";
    }

    /**
     * 교수용 최근 로그인 시간이 “HH:mm” 형식으로,
     *         접속 중이 아니면 "미접속"을 반환
     */
    public String getConnectionTime(Long studentNumber, Long classroomId) {
        // 1) studentClassroom 조회
        StudentClassroom sc = scRepository
                .findByStudent_StudentNumberAndClassroom_Id(studentNumber, classroomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 학번의 학생이 이 강의실에 없습니다."
                ));

        // 2) LOGIN/LOGOUT 로그만 필터
        return logRepository.findByStudentClassroom_Id(sc.getId()).stream()
                .filter(l -> "LOGIN".equalsIgnoreCase(l.getLogType()) ||
                        "LOGOUT".equalsIgnoreCase(l.getLogType()))
                // 3) timestamp 파싱 후 정렬
                .sorted(Comparator.comparing(l ->
                        LocalDateTime.parse(l.getTimestamp(), PARSER)))
                // 4) 마지막 이벤트
                .reduce((first, second) -> second)
                .map(lastLog -> {
                    if ("LOGOUT".equalsIgnoreCase(lastLog.getLogType())) {
                        return "미접속";
                    }
                    // 로그인인 경우 시간만 HH:mm 형식으로 반환
                    LocalDateTime loginTime = LocalDateTime.parse(lastLog.getTimestamp(), PARSER);
                    return loginTime.format(TIME_FORMATTER);
                })
                .orElse("미접속");
    }


    /**
     * 시험제출 로그에서 시간(HH:mm)만 뽑아 반환
     */
    public List<String> getSubmissionTimes(String name,
                                           Long studentNumber,
                                           Long examId) {
        // 1) 학생 조회 (이름으로 검증하고 싶으면 name 파라미터도 같이 쓰세요)
        Student student = studentRepository
                .findByNameAndStudentNumber(name, studentNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 학생을 찾을 수 없습니다."
                ));

        // 2) ExamInfo 조회
        ExamInfo examInfo = examInfoRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 시험 정보를 찾을 수 없습니다."
                ));

        // 3) StudentClassroom 조회 (ExamInfo에 연결된 강의실 ID 사용)
        Long classroomId = examInfo.getClassroom().getId();
        StudentClassroom sc = scRepository
                .findByStudent_StudentNumberAndClassroom_Id(student.getStudentNumber(), classroomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 학생이 이 강의실에 등록되어 있지 않습니다."
                ));

        // 4) 로그 조회 + 필터링 + 포맷팅
        return logRepository
                .findByStudentClassroom_IdAndExamInfo_Id(sc.getId(), examId)
                .stream()
                // 시험제출 로그만
                .filter(l -> "SAVE_EXAM".equalsIgnoreCase(l.getLogType()))
                // Timestamp 문자열 → LocalDateTime 파싱 → "HH:mm" 포맷
                .map(l -> LocalDateTime
                        .parse(l.getTimestamp(), PARSER)
                        .format(TIME_FORMATTER))
                .collect(Collectors.toList());
    }

    public long calculateRemainingTime(Long studentId, Long examInfoId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생이 존재하지 않습니다."));
        ExamInfo examInfo = examInfoRepository.findById(examInfoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시험 정보가 존재하지 않습니다."));

        int durationInSeconds = examInfo.getDuration() * 60;

        List<Log> logs = logRepository.findByStudentIdAndExamInfoIdOrderByTimestampAsc(studentId, examInfoId);

        LocalDateTime startTime = null;
        long pausedDuration = 0;
        LocalDateTime lastExitTime = null;

        for (Log log : logs) {
            if (log.getTimestamp() == null) continue;

            String type = log.getLogType();
            LocalDateTime time = LocalDateTime.parse(log.getTimestamp());

            if (type.equals("IN_EXAM") && startTime == null) {
                startTime = time;
            } else if (type.equals("EXAM_EXIT")) {
                lastExitTime = time;
            } else if (type.equals("IN_EXAM") && lastExitTime != null) {
                pausedDuration += Duration.between(lastExitTime, time).getSeconds();
                lastExitTime = null;
            }
        }

        // 처음 입장 시
        if (startTime == null) {
            startTime = LocalDateTime.now();

            Log newLog = new Log();
            newLog.setStudent(student);
            newLog.setExamInfo(examInfo);
            newLog.setLogType("IN_EXAM");
            newLog.setTimestamp(startTime.toString());
            logRepository.save(newLog);

            return durationInSeconds;
        }

        long elapsed = Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long effective = elapsed - pausedDuration;
        return Math.max(0, durationInSeconds - effective);
    }

    public enum ExamStatus {
        BEFORE,
        IN_PROGRESS,
        FINISHED
    }


    public ExamStatus determineExamStatus(Long studentId, Long examInfoId) {
        List<Log> logs = logRepository.findByStudentIdAndExamInfoIdOrderByTimestampAsc(studentId, examInfoId);

        boolean hasStarted = false;
        boolean hasSubmitted = false;

        LocalDateTime startTime = null;
        long pausedDuration = 0;
        LocalDateTime lastExit = null;

        for (Log log : logs) {
            String type = log.getLogType();
            LocalDateTime time = LocalDateTime.parse(log.getTimestamp());

            if (type.equals("IN_EXAM") && startTime == null) {
                startTime = time;
                hasStarted = true;
            } else if (type.equals("EXAM_EXIT")) {
                lastExit = time;
            } else if (type.equals("IN_EXAM") && lastExit != null) {
                pausedDuration += Duration.between(lastExit, time).getSeconds();
                lastExit = null;
            } else if ("SAVE_EXAM".equalsIgnoreCase(type.trim())) {
                hasSubmitted = true;
            }
        }

        if (!hasStarted) return ExamStatus.BEFORE;
        if (hasSubmitted) return ExamStatus.FINISHED;

        // 남은 시간 확인
        ExamInfo exam = examInfoRepository.findById(examInfoId)
                .orElseThrow(() -> new IllegalArgumentException("시험 없음"));

        int duration = exam.getDuration() * 60;
        long elapsed = Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long effective = elapsed - pausedDuration;

        if (effective >= duration) return ExamStatus.FINISHED;

        return ExamStatus.IN_PROGRESS;
    }




}
