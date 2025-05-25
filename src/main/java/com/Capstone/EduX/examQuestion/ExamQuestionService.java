package com.Capstone.EduX.examQuestion;

import com.Capstone.EduX.examInfo.ExamInfoService;
import com.Capstone.EduX.examResult.ExamResult;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamInfoService examInfoService;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository
    , ExamInfoService examInfoService) {
        this.examQuestionRepository = examQuestionRepository;
        this.examInfoService  = examInfoService;
    }

    public ExamQuestion save(ExamQuestion question) {
        return examQuestionRepository.save(question);
    }

    public List<ExamQuestion> findByExamId(Long examId) {
        return examQuestionRepository.findByExamId(examId);
    }

    public List<ExamQuestion> findAll() {
        return examQuestionRepository.findAll();
    }

    public List<ExamQuestion> saveAll(List<ExamQuestion> questions) {
        return examQuestionRepository.saveAll(questions); // MongoRepository 기본 제공 메서드
    }

    public ExamQuestion findById(String id) {
        return examQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 문제를 찾을 수 없습니다: " + id));
    }

    //단일저장시 사용
    @Transactional
    public ExamQuestion autoSaveOne(ExamQuestion q) {
        ExamQuestion saved;
        if (q.getId() != null && examQuestionRepository.existsById(q.getId())) {
            // 이미 저장된 문제 → 수정
            ExamQuestion existing = examQuestionRepository.findById(q.getId()).get();

            existing.setQuestion(q.getQuestion());
            existing.setAnswer(q.getAnswer());
            existing.setDistractor(q.getDistractor());
            existing.setType(q.getType());
            existing.setQuestionScore(q.getQuestionScore());
            existing.setNumber(q.getNumber());
            existing.setExamId(q.getExamId());

            saved = examQuestionRepository.save(existing);
        } else {
            // 새 문제 저장
            saved = examQuestionRepository.save(q);
        }

        // **문제 개수 갱신** (현재 해당 examId로 저장된 총 개수)
        long count = examQuestionRepository.countByExamId(saved.getExamId());
        examInfoService.updateQuestionCount(saved.getExamId(), (int) count);

        return saved;
    }

    @Transactional
    //다중 저장 시 사용(저장된 문제와 아닌 문제 구분)
    public List<ExamQuestion> autoSaveBulk(List<ExamQuestion> questions) {
        List<ExamQuestion> result = new ArrayList<>();

        for (ExamQuestion q : questions) {
            if (q.getId() != null && examQuestionRepository.existsById(q.getId())) {
                // ① 이미 저장된 문제 → 수정 (덮어쓰기)
                ExamQuestion existing = examQuestionRepository.findById(q.getId()).get();

                existing.setQuestion(q.getQuestion());
                existing.setAnswer(q.getAnswer());
                existing.setDistractor(q.getDistractor());
                existing.setType(q.getType());
                existing.setQuestionScore(q.getQuestionScore());
                existing.setNumber(q.getNumber());
                existing.setExamId(q.getExamId());

                // 수정된 문제를 저장하고 결과 리스트에 추가
                result.add(examQuestionRepository.save(existing));
            } else {
                result.add(examQuestionRepository.save(q));
            }
        }

        // **문제 개수 갱신** (bulk로 저장된 총 개수)
        if (!result.isEmpty()) {
            Long examId = result.get(0).getExamId();
            examInfoService.updateQuestionCount(examId, result.size());
        }

        return result;
    }
    //답안 제외 문제 불러오기
    public List<Map<String, Object>> getQuestionsWithoutAnswer(Long examId) {
        List<ExamQuestion> questions = examQuestionRepository.findByExamId(examId);

        return questions.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("number", q.getNumber());
            map.put("question", q.getQuestion());
            map.put("distractor", q.getDistractor()); // 옵션 리스트
            map.put("type", q.getType());
            map.put("questionScore", q.getQuestionScore());
            return map; // 정답(answer)는 포함 ❌
        }).collect(Collectors.toList());
    }

}