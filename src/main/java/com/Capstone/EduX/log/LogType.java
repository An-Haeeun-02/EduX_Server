package com.Capstone.EduX.log;

public enum LogType {
    LOGIN, //로그인
    LOGOUT, //로그아웃
    IN_CLASSROOM, //강의실 가입
    WAITING_EXAM, //시험대기
    IN_EXAM, // 시험 입장
    TEMPORARY_STORAGE, //시험 임시저장
    SAVE_EXAM, //시험 제출
    CHEAT, //시험 중 잘못 된 접금
    CHECK_RESULT, //시험 결과 확인
    EXAM_EXIT //의도치 않은 나감
}