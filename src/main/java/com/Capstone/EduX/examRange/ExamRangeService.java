package com.Capstone.EduX.examRange;

import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExamRangeService {

    private final ExamRangeRepository repository;
    private final ExamInfoRepository examInfoRepository;

    public ExamRangeService(ExamRangeRepository repository, ExamInfoRepository examInfoRepository) {
        this.repository = repository;
        this.examInfoRepository = examInfoRepository;
    }

    @Transactional
    public void saveRangeDetails(Long examId, List<String> rangeDetails) {
        //시험 ID로 ExamInfo를 찾음
        ExamInfo examInfo = examInfoRepository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("시험을 찾을 수 없습니다."));

        //기존 범위 모두 삭제
        repository.deleteByExamInfo(examInfo);

        //저장할 ExamRange 리스트를 준비
        List<ExamRange> ranges = new ArrayList<>();

        //rangeDetail 하나하나를 ExamRange 객체로 만들어 리스트에 추가
        for (String detail : rangeDetails) {
            ExamRange range = new ExamRange();
            range.setExamInfo(examInfo);       // 시험과 연결
            range.setRangeDetail(detail);      // 범위 텍스트 저장
            ranges.add(range);
        }

        repository.saveAll(ranges);
    }
}
