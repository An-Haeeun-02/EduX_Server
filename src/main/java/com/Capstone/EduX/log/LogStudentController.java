package com.Capstone.EduX.log;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.StudentClassroom.StudentClassroom;
import com.Capstone.EduX.examInfo.ExamInfo;
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
public class LogStudentController {

    private final LogService logService;
    private final LogRepository logRepository;

    @PostMapping("/login")
    public ResponseEntity<String> logLogin(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.LOGIN,
                null,
                null,
                "로그인 완료"
        );
        return ResponseEntity.ok("로그인 로그 저장 완료");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logLogout(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.LOGOUT,
                null,
                null,
                "로그아웃 완료"
        );
        return ResponseEntity.ok("로그아웃 로그 저장 완료");
    }

    @PostMapping("/in-classroom")
    public ResponseEntity<String> logInClassroom(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.IN_CLASSROOM,
                Long.parseLong(request.get("classroomId")),
                null,
                "강의실 가입 완료"
        );
        return ResponseEntity.ok("강의실 가입 로그 저장 완료");
    }

    @PostMapping("/waiting-exam")
    public ResponseEntity<String> logWaitingExam(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.WAITING_EXAM,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "시험 대기 중"
        );
        return ResponseEntity.ok("시험 대기 로그 저장 완료");
    }

    @PostMapping("/in-exam")
    public ResponseEntity<String> logInExam(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.IN_EXAM,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "시험 입장 완료"
        );
        return ResponseEntity.ok("시험 입장 로그 저장 완료");
    }

    @PostMapping("/temporary-storage")
    public ResponseEntity<String> logTemporaryStorage(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.TEMPORARY_STORAGE,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "답안 임시 저장"
        );
        return ResponseEntity.ok("임시저장 로그 저장 완료");
    }

    @PostMapping("/submit-exam")
    public ResponseEntity<String> logSubmitExam(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.SAVE_EXAM,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "시험 제출 완료"
        );
        return ResponseEntity.ok("시험 제출 로그 저장 완료");
    }

    @PostMapping("/cheat")
    public ResponseEntity<String> logCheat(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.CHEAT,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                request.get("detail")
        );
        return ResponseEntity.ok("부정행위 로그 저장 완료");
    }

    @PostMapping("/check-result")
    public ResponseEntity<String> logCheckResult(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.CHECK_RESULT,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "시험 결과 확인"
        );
        return ResponseEntity.ok("결과 확인 로그 저장 완료");
    }

    @PostMapping("/exit-exam")
    public ResponseEntity<String> logExitExam(@RequestBody Map<String, String> request) {
        logService.saveLog(
                request.get("studentId"),
                LocalDateTime.parse(request.get("timestamp")),
                LogType.EXAM_EXIT,
                Long.parseLong(request.get("classroomId")),
                Long.parseLong(request.get("examId")),
                "시험 도중 페이지 이탈"
        );
        return ResponseEntity.ok("이탈 로그 저장 완료");
    }







}