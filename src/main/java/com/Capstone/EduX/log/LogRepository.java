package com.Capstone.EduX.log;

import com.Capstone.EduX.StudentClassroom.StudentClassroom;
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

    // StudentClassroom PK 로 해당 로그들 조회
    List<Log> findByStudentClassroom_Id(Long studentClassroomId);

    // 학생별 + 시험별 로그 조회
    List<Log> findByStudentClassroom_IdAndExamInfo_Id(Long studentClassroomId, Long examInfoId);

    //시험 일시정지 등
    List<Log> findByStudentIdAndExamInfoIdOrderByTimestampAsc(Long studentId, Long examInfoId);

    // 상태 확인용: 가장 최근의 IN_EXAM, SAVE_EXAM, EXAM_EXIT 로그
    Log findTopByStudentClassroomAndExamInfoIdAndLogTypeInOrderByTimestampDesc(
            StudentClassroom studentClassroom,
            Long examInfoId,
            List<String> logTypes
    );

    // 접속 시간 확인용: 가장 최근 IN_EXAM 로그
    Log findTopByStudentClassroomAndExamInfoIdAndLogTypeOrderByTimestampDesc(
            StudentClassroom studentClassroom,
            Long examInfoId,
            String logType
    );

    List<Log> findByStudentClassroomIdOrderByTimestampAsc(Long studentClassroomId);

    List<Log> findByStudentClassroomIdAndExamInfoId(Long studentClassroomId, Long examInfoId);

}
