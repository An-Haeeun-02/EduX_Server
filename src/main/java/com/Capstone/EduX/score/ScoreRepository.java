package com.Capstone.EduX.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    // 한 학생이 한 시험에서 받은 점수 정보를 조회
    Optional<Score> findByStudentIdAndExamInfoId(Long studentId, Long examInfoId);
}
