package com.Capstone.EduX.examQuestion;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository) {
        this.examQuestionRepository = examQuestionRepository;
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
    public ExamQuestion autoSaveOne(ExamQuestion q) {
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

            return examQuestionRepository.save(existing); // 수정 후 반환
        } else {
            // 새 문제 → 새로 저장
            return examQuestionRepository.save(q);
        }
    }

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
                // ② 새 문제 → insert
                result.add(examQuestionRepository.save(q));
            }
        }

        return result;
    }
}