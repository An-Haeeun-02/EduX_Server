package com.Capstone.EduX.examResult;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-result")
public class ExamResultController {

    private final ExamResultService examResultService;

    public ExamResultController(ExamResultService examResultService) {
        this.examResultService = examResultService;
    }

    //저장된 답안 불러오기
    @GetMapping("/answers") //Query Parameter
    public ResponseEntity<?> getUserAnswers(
            @RequestParam Long examId,
            @RequestParam Long userId
    ) {
        try {// 답변 리스트화
            List<Map<String, Object>> result = examResultService.getUserAnswers(examId, userId);
            return ResponseEntity.ok(result);

        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getReason());
        }
    }

    //답 저장하기(단일)
    @PostMapping("/save")
    public ResponseEntity<?> saveAnswer(@RequestBody Map<String, Object> body) {
        try {
            Long examId = Long.valueOf(body.get("examId").toString());
            Long userId = Long.valueOf(body.get("userId").toString());
            String examQuestionId = body.get("examQuestionId").toString();
            String userAnswer = body.get("userAnswer").toString();

            Long resultId = examResultService.saveOrUpdateAnswer(examId, userId, examQuestionId, userAnswer);

            return ResponseEntity.ok(Map.of("id", resultId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("답안 저장 중 오류가 발생했습니다.");
        }
    }

    //답 저장하기(다수)
    @PostMapping("/save/multiple")
    public ResponseEntity<?> saveMultiple(
            @RequestBody Map<String, Object> reqBody
    ) {
        try {
            // 1) 요청 바디에서 값 꺼내기
            Long examId = ((Number)reqBody.get("examId")).longValue();
            Long userId = ((Number)reqBody.get("userId")).longValue();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> answers =
                    (List<Map<String, Object>>)reqBody.get("answers");

            // 2) Service 호출
            List<Long> savedIds = examResultService.saveMultipleAnswers(
                    examId, userId, answers
            );
            return ResponseEntity.ok(Map.of("savedIds", savedIds));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("여러 답안 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentExamResultGrades(@RequestParam Long examId,
                                                        @RequestParam Long userId) {
        List<Map<String, Object>> result = examResultService.getIsGradeListByStudentAndExam(userId, examId);
        return ResponseEntity.ok(result);
    }

}
