package com.Capstone.EduX.gradingResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grading")
public class GradingResultController {

    private final GradingResultService gradingResultService;

    public GradingResultController(GradingResultService gradingResultService) {
        this.gradingResultService = gradingResultService;
    }

    //  학생의 이름 + 학번으로 해당 시험에 대한 문제 + 학생의 답안 불러오기
    @GetMapping("/view")
    public ResponseEntity<?> getExamResults(@RequestParam String name,
                                            @RequestParam String studentNumber,
                                            @RequestParam Long examId) {
        return ResponseEntity.ok(gradingResultService.getStudentExamAnswers(name, studentNumber, examId));
    }

    // 수동채점 결과 저장
    @PostMapping("/grade")
    public ResponseEntity<?> submitGrade(@RequestBody Map<String, Object> req) {
        String examQuestionId = req.get("examQuestionId").toString();
        Long examResultId = Long.valueOf(req.get("examResultId").toString());
        Integer scoreInput = Integer.parseInt(req.get("score").toString());

        gradingResultService.gradeWithScore(examQuestionId, examResultId, scoreInput);

        if (gradingResultService.isAllGraded(examResultId)) {
            gradingResultService.ensureScoreExists(examResultId);
            gradingResultService.updateTotalScore(examResultId);
        }

        return ResponseEntity.ok("채점 완료");
    }
    //현재 채점 현황 O,x,세모로 표시
    @GetMapping("/status")
    public ResponseEntity<?> getGradingStatus(
            @RequestParam Long examId,
            @RequestParam Long studentId) {

        List<Map<String, Object>> statusList = gradingResultService.getGradingStatus(examId, studentId);
        return ResponseEntity.ok(statusList);
    }
    //자동채점
    @PostMapping("/autograde")
    public ResponseEntity<?> autoGradeObjectiveAnswers(@RequestParam String name,
                                                       @RequestParam String studentNumber,
                                                       @RequestParam Long examId) {
        gradingResultService.autoGradeForStudent(name, studentNumber, examId);
        return ResponseEntity.ok("자동 채점 완료");
    }

    // 총점 조회
    @GetMapping("/total-score")
    public ResponseEntity<?> getTotalScore(@RequestParam String name,
                                           @RequestParam String studentNumber,
                                           @RequestParam Long examId) {
        Integer totalScore = gradingResultService.getTotalScore(name, studentNumber, examId);
        return ResponseEntity.ok(Map.of("totalScore", totalScore));
    }

}