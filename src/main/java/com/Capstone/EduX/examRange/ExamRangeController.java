package com.Capstone.EduX.examRange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-range")
public class ExamRangeController {

    private final ExamRangeService service;

    public ExamRangeController(ExamRangeService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRanges(@RequestBody Map<String, Object> request) {
        try {
            // 전달받은 JSON에서 examId 추출
            Long examId = Long.valueOf(request.get("examId").toString());

            // rangeDetails를 문자열 리스트
            @SuppressWarnings("unchecked")
            List<String> rangeDetails = (List<String>) request.get("rangeDetails");

            //서비스 저장
            service.saveRangeDetails(examId, rangeDetails);

            // 성공 응답
            return ResponseEntity.ok("허용 범위가 저장되었습니다.");
        } catch (Exception e) {
            // 예외 발생
            return ResponseEntity.badRequest().body("저장 실패: " + e.getMessage());
        }
    }
}
