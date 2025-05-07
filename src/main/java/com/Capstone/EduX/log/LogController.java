package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.examInfo.ExamInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

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


}
