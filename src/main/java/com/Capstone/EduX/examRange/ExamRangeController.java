package com.Capstone.EduX.examRange;

import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/exam-range")
public class ExamRangeController {

    private final ExamRangeService service;
    private final ExamInfoRepository examInfoRepo;

    // • service와 examInfoRepo 둘 다 주입되도록 생성자 수정
    public ExamRangeController(ExamRangeService service,
                               ExamInfoRepository examInfoRepo) {
        this.service = service;
        this.examInfoRepo = examInfoRepo;
    }

    // 저장 시 mode와 rangeDetails 모두 덮어쓰기
    @PostMapping("/save")
    public ResponseEntity<?> saveRanges(@RequestBody Map<String, Object> request) {
        Long examId = Long.valueOf(request.get("examId").toString());
        String mode = request.get("mode").toString();

        @SuppressWarnings("unchecked")
        List<String> rangeDetails = (List<String>) request.get("rangeDetails");

        // 1) ExamInfo.accessMode 업데이트
        ExamInfo exam = examInfoRepo.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("시험을 찾을 수 없습니다."));
        exam.setAccessMode(mode);
        examInfoRepo.save(exam);

        // 2) 상세 범위 목록 덮어쓰기
        service.saveRangeDetails(examId, rangeDetails);

        return ResponseEntity.ok("허용 범위가 저장되었습니다.");
    }

    // 조회 시 mode와 목록을 함께 리턴
    @GetMapping("/{examId}")
    public ResponseEntity<Map<String, Object>> getRanges(@PathVariable Long examId) {
        // 1) ExamInfo에서 현재 mode 조회
        ExamInfo exam = examInfoRepo.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("시험을 찾을 수 없습니다."));
        String mode = exam.getAccessMode();

        // 2) 서비스에서 상세 목록 조회
        List<String> details = service.getRangeDetails(examId);

        // 3) 두 값을 묶어서 응답
        Map<String, Object> response = new HashMap<>();
        response.put("mode", mode);
        response.put("rangeDetails", details);

        return ResponseEntity.ok(response);
    }
}
