package com.Capstone.EduX.examQuestion;

import com.Capstone.EduX.examQuestion.dto.ExamQuestionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam-questions")
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
    public Map<String, List<Map<String,Object>>> autoSaveBulk(
            @RequestBody List<ExamQuestion> questions) {
        List<ExamQuestion> savedList = examQuestionService.autoSaveBulk(questions);

        List<Map<String,Object>> results = savedList.stream()
                .map(q -> {
                    Map<String,Object> m = new HashMap<>();
                    m.put("id", q.getId());
                    m.put("number", q.getNumber());
                    return m;
                })
                .collect(Collectors.toList());

        return Collections.singletonMap("results", results);
    }

    //단일저장, id 중복 대응
    @PostMapping("/autosave")
    public ResponseEntity<?> autoSaveOne(@RequestBody ExamQuestion q) {
        try {
            ExamQuestion saved = examQuestionService.autoSaveOne(q);
            return ResponseEntity.ok(Map.of("id", saved.getId()));
        } catch (Exception e) {
            e.printStackTrace();  // 콘솔에 스택트레이스 출력
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));  // 에러 메시지를 JSON으로 클라이언트에 전달
        }
    }


    //시험 문제 조회(답안 제외)
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Map<String, Object>>> getQuestions(@PathVariable Long examId) {
        List<Map<String, Object>> questions = examQuestionService.getQuestionsWithoutAnswer(examId);

        // 항상 200 OK, 빈 리스트일 수도 있음
        return ResponseEntity.ok(questions);
    }

    //시험 문제 조회(전체)
    @GetMapping("/exam/all/{examId}")
    public ResponseEntity<List<ExamQuestion>> getQuestionsWithAnswer(@PathVariable Long examId) {
        List<ExamQuestion> questions = examQuestionService.findByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping // 전체 문제 리스트 가져오기
    public List<ExamQuestion> getAll() {
        return examQuestionService.findAll();
    }

    // 단일 문제 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        examQuestionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exam/{examId}/all")
    public ResponseEntity<List<ExamQuestion>> getByExamId(@PathVariable Long examId) {
        List<ExamQuestion> list = examQuestionService.findByExamId(examId);
        return ResponseEntity.ok(list);
    }

}
