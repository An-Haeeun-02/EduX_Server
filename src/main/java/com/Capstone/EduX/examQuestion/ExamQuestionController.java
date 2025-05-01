package com.Capstone.EduX.examQuestion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exam-questions")
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    public ExamQuestionController(ExamQuestionService examQuestionService) {
        this.examQuestionService = examQuestionService;
    }

    @GetMapping("/{id}") //id로 문제 조회
    public ResponseEntity<ExamQuestion> getOneQuestion(@PathVariable String id) {
        ExamQuestion question = examQuestionService.findById(id);
        return ResponseEntity.ok(question);
    }


    @PostMapping //문제 저장(믄제하나)
    public ExamQuestion createQuestion(@RequestBody ExamQuestion question) {
        return examQuestionService.save(question);
    }

//    @PostMapping("/bulk")//문제 저장(여러문제)
//    public List<ExamQuestion> saveMultipleQuestions(@RequestBody List<ExamQuestion> questions) {
//        return examQuestionService.saveAll(questions);
//    }

    //다중 저장
    @PostMapping("/autosave/bulk")
    public Map<String, List<Map<String, Object>>> autoSaveBulk(@RequestBody List<ExamQuestion> questions) {
        List<ExamQuestion> savedList = examQuestionService.autoSaveBulk(questions);

        List<Map<String, Object>> results = savedList.stream()
                .map(q -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", q.getId());
                    map.put("number", q.getNumber());
                    return map;
                })
                .collect(Collectors.toList());

        return Collections.singletonMap("results", results);
    }


    //단일저장, id 중복 대응
    @PostMapping("/autosave")
    public Map<String, String> autoSaveOne(@RequestBody ExamQuestion question) {
        ExamQuestion saved = examQuestionService.autoSaveOne(question);
        return Map.of("id", saved.getId()); // key: id, value: 실제 ID
    }

    @GetMapping("/exam/{examId}") // /api/exam-question/exam/1
    public ResponseEntity<?> getQuestions(@PathVariable Long examId) {
        List<Map<String, Object>> questions = examQuestionService.getQuestionsWithoutAnswer(examId);

        if (questions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 시험의 문제가 없습니다.");
        }

        return ResponseEntity.ok(questions); // 정답 없이 문제만 보냄
    }


    @GetMapping // 전체 문제 리스트 가져오기
    public List<ExamQuestion> getAll() {
        return examQuestionService.findAll();
    }


}
