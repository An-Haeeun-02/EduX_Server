package com.Capstone.EduX.log;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor/classrooms/{classroomId}/students/{studentNumber}/logs")
@RequiredArgsConstructor
public class LogController_professor {

    private final LogService logService;

    @GetMapping
    public ResponseEntity<List<LogDto>> fetchStudentLogs(
            @PathVariable Long classroomId,
            @PathVariable Long studentNumber) {
        return ResponseEntity.ok(
                logService.getLogsForProfessor(studentNumber, classroomId)
        );
    }

    //시험 상태 가져오기
    @GetMapping("/status/{examInfoId}")
    public ResponseEntity<String> getExamStatus(
            @PathVariable Long classroomId,
            @PathVariable Long studentNumber,
            @PathVariable Long examInfoId
    ) {
        String status = logService.getExamStatus(studentNumber, classroomId, examInfoId);
        return ResponseEntity.ok(status);
    }

    //접속 상태 조회

    @GetMapping("/connection")
    public ResponseEntity<String> getConnectionTime(
            @PathVariable Long classroomId,
            @PathVariable Long studentNumber
    ) {
        String result = logService.getConnectionTime(studentNumber, classroomId);
        return ResponseEntity.ok(result);
    }

}

