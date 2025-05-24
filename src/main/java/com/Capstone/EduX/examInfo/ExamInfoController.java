package com.Capstone.EduX.examInfo;

import com.Capstone.EduX.examInfo.dto.ExamCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/exams")
public class ExamInfoController {

    private final ExamInfoService service;
    private final ExamInfoService examInfoService;

    public ExamInfoController(ExamInfoService service, ExamInfoService examInfoService) {
        this.service = service;
        this.examInfoService = examInfoService;
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

    //시험 정보 업데이트
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

    //시험 대기 접근 활성화 여부
    @GetMapping("/{examID}/access")
    public ResponseEntity<?> checkIdleExamAccess(@PathVariable Long examID) {
        try {
            Map<String, Object> result = examInfoService.checkIdleExamAccess(examID);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    //시험 접근 활성화 여부
    @GetMapping("/{examID}/accessTest")
    public ResponseEntity<?> checkExamAccess(@PathVariable Long examID) {
        try {
            Map<String, Object> result = examInfoService.checkExamAccess(examID);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    //시험 목록 조회
    @GetMapping("/examList")
    public ResponseEntity<?> getExamTitles(
            @RequestParam String studentId,
            @RequestParam Long classroomId
    ) {
        try {
            List<Map<String, Object>> result = examInfoService.getExamTitles(studentId, classroomId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //시험 삭제(정보만)
    @DeleteMapping("/delete/{examId}")
    public ResponseEntity<?> deleteExam(@PathVariable Long examId) {
        try {
            service.deleteExam(examId);
            return ResponseEntity.ok("삭제 완료");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("해당 시험이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    //시험 삭제(정보, 문제, 범위)
    @DeleteMapping("/delete/all/{examId}")
    public ResponseEntity<?> deleteExamAll(@PathVariable Long examId) {
        try {
            service.deleteExamCascade(examId);
            return ResponseEntity.ok("시험 및 관련 데이터 삭제 완료");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("시험 없음");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    //저장된 시험 정보 불러오기
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExamById(@PathVariable Long id) {
        ExamInfo exam = examInfoService.getExamById(id);

        // 🔸 외래키를 제외하고 필요한 정보만 Map으로 수동 구성
        Map<String, Object> response = new HashMap<>();
        response.put("id", exam.getId());
        response.put("title", exam.getTitle());
        response.put("startTime", exam.getStartTime());
        response.put("endTime", exam.getEndTime());
        response.put("testStartTime", exam.getTestStartTime());
        response.put("testEndTime", exam.getTestEndTime());
        response.put("notice", exam.getNotice());
        response.put("questionCount", exam.getQuestionCount());

        return ResponseEntity.ok(response);
    }

}
