package com.Capstone.EduX.log;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    // 1. 강의실 ID로 로그 조회
    List<Log> findByClassroomId(Long classroomId);

    // 2. StudentClassroom ID로 로그 조회
    List<Log> findByStudentClassroomId(Long studentClassroomId);

    // 3. 시험 정보 ID로 로그 조회
    List<Log> findByExamInfoId(Long examInfoId);

    // 4. 로그 타입으로 조회
    List<Log> findByLogType(String logType);
}
