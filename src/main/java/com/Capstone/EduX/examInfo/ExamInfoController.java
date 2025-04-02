package com.Capstone.EduX.examInfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
