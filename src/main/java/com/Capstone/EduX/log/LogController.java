package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.student.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;
    private final LogRepository logRepository;

    //시스템 로그 저장 작동 확인
    @GetMapping("/error-test")
    public String healthCheck() {
        log.info("서버 정상 작동 중입니다.");  // 파일로 저장
        return "OK";
    }

    // 전체 로그 조회 API
    @GetMapping
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    // 1. 강의실 ID로 로그 보기
    @GetMapping("/classroom/{classroomId}")
    public List<Map<String, Object>> getLogsByClassroomId(@PathVariable Long classroomId) {
        List<Log> logs = logService.getLogsByClassroomId(classroomId);

        return logs.stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("logType", log.getLogType());
                    map.put("timestamp", log.getTimestamp());
                    map.put("detail", log.getDetail());
                    return map;
                })
                .toList();
    }


    // 2. StudentClassroom ID로 로그 보기
    @GetMapping("/studentClassroom/{studentClassroomId}")
    public List<Map<String, Object>> getLogsByStudentClassroomId(@PathVariable Long studentClassroomId) {
        List<Log> logs = logService.getLogsByStudentClassroomId(studentClassroomId);
        return logs.stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("logType", log.getLogType());
                    map.put("timestamp", log.getTimestamp());
                    map.put("detail", log.getDetail());
                    return map;
                })
                .toList();
    }

    // 3. 시험 정보 ID로 로그 보기
    @GetMapping("/exam/{examInfoId}")
    public List<Map<String, Object>> getLogsByExamInfoId(@PathVariable Long examInfoId) {
        List<Log> logs = logService.getLogsByExamInfoId(examInfoId);
        return logs.stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("logType", log.getLogType());
                    map.put("timestamp", log.getTimestamp());
                    map.put("detail", log.getDetail());
                    return map;
                })
                .toList();
    }

    // 4. 로그 타입으로 보기
    @GetMapping("/type/{logType}")
    public List<Map<String, Object>> getLogsByLogType(@PathVariable String logType) {
        List<Log> logs = logService.getLogsByLogType(logType);
        return logs.stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("logType", log.getLogType());
                    map.put("timestamp", log.getTimestamp());
                    map.put("detail", log.getDetail());
                    return map;
                })
                .toList();
    }

    /**
     * @param name 학생 이름 (옵션)
     * @param studentNumber 학번
     * @param examId 시험 ID (ExamInfo의 PK)
     * @return 해당 학생의 시험제출 로그 시간(시:분) 목록
     * 학번 + 시험 ID 로 해당 학생의 시험제출시간 조회
     */
    @GetMapping("/submission-times")
    public ResponseEntity<List<String>> getSubmissionTimes(
            @RequestParam(required = false) String name,
            @RequestParam Long studentNumber,
            @RequestParam Long examId) {

        List<String> times = logService.getSubmissionTimes(name, studentNumber, examId);
        return ResponseEntity.ok(times);
    }

    //남은 시간
    @GetMapping("/remaining-time")
    public ResponseEntity<Long> getRemainingTime(
            @RequestParam Long studentId,
            @RequestParam Long examInfoId
    ) {
        long remaining = logService.calculateRemainingTime(studentId, examInfoId);
        return ResponseEntity.ok(remaining);
    }

    //시험 상태
    @GetMapping("/exam-status")
    public ResponseEntity<String> getExamStatus(
            @RequestParam Long studentId,
            @RequestParam Long examInfoId,
            @RequestParam Long classroomId
    ) {
        LogService.ExamStatus status = logService.determineExamStatus(studentId, examInfoId, classroomId);
        return ResponseEntity.ok(status.name());
    }

    //로그용 시험 상태
    @GetMapping("/in-exam-status")
    public ResponseEntity<List<Map<String, Object>>> getStudentExamStatus(
            @RequestParam Long examId,
            @RequestParam Long classroomId
    ) {
        List<Map<String, Object>> statusList = logService.getStudentExamStatus(examId, classroomId);
        return ResponseEntity.ok(statusList);
    }

    @PostMapping("/test")
    public ResponseEntity<String> insertTestLog(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.now(),
                LogType.CHEAT, // ✅ 실시간 알림용
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "임시 테스트 로그"
        );

        return ResponseEntity.ok("테스트 로그 저장 완료");
    }

    @GetMapping("/student-logs")
    public ResponseEntity<List<Map<String, Object>>> getStudentLogs(
            @RequestParam Long examId,
            @RequestParam Long classroomId,
            @RequestParam Long studentId // ✅ PK 사용
    ) {
        List<Map<String, Object>> logs = logService.getStudentLogs(examId, classroomId, studentId);
        return ResponseEntity.ok(logs);
    }




}
