package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/exams")
public class ExamInfoController {

    private final ExamInfoService service;

    public ExamInfoController(ExamInfoService service) {
        this.service = service;
    }

    //활성화 된 강의실에서
//    @GetMapping("/active/{classroomId}")
//    public ResponseEntity<List<Map<String, Object>>> getActiveExams(@PathVariable Long classroomId) {
//        List<ExamInfo> exams = service.getActiveExams(classroomId);
//
//        List<Map<String, Object>> result = exams.stream().map(e -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", e.getId());
//            map.put("title", e.getTitle());
//            return map;
//        }).toList();
//
//        return ResponseEntity.ok(result);
//    }

    @PostMapping("/create")
    public ResponseEntity<ExamInfo> createExam(@RequestBody ExamCreateRequest request) {
        ExamInfo exam = service.createExam(request);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/{professorId}/{classroomId}/list")
    public ResponseEntity<List<ExamInfo>> getExamsByClassroomIdAndProfessorId(
            @PathVariable Long professorId,
            @PathVariable Long classroomId) {

        List<ExamInfo> exams = service.getExamsByClassroomIdAndProfessorId(classroomId, professorId);
        return ResponseEntity.ok(exams);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateExam(@RequestBody Map<String, Object> request) {
        try {
            String title = service.updateExamInfo(request);
            return ResponseEntity.ok("시험 수정 완료: " + title);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("시험을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("시험 수정 실패: " + e.getMessage());
        }
    }



}
