package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.student.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professor/student-classrooms")
public class StudentClassroomController_professor {

    private final StudentClassroomService studentClassroomService;

    public StudentClassroomController_professor(StudentClassroomService studentClassroomService) {
        this.studentClassroomService = studentClassroomService;
    }

    // 강의실 ID로 학생 명단 조회
    @GetMapping("/classroom/{classroomId}/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentsByClassroomId(@PathVariable Long classroomId) {
        List<Student> students = studentClassroomService.getStudentsByClassroomId(classroomId);

        List<Map<String, Object>> result = students.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", s.getStudentId());
            map.put("name", s.getName());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // 시험에만 속한 학생 조회
    @GetMapping("/exam/{examId}/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentsByExamId(@PathVariable Long examId) {
        List<Student> students = studentClassroomService.getStudentsByExamId(examId);

        List<Map<String, Object>> result = students.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", s.getStudentId());
            map.put("name", s.getName());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // 강의실에서 학생 강퇴
    @DeleteMapping("/classroom/{classroomId}/student/{studentId}")
    public ResponseEntity<Map<String, Object>> removeStudentFromClassroom(@PathVariable Long classroomId,
                                                                          @PathVariable String studentId) {
        studentClassroomService.removeStudentFromClassroom(studentId, classroomId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "학생이 강퇴되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 강의실 학생 목록 조회(시험제출시간, 점수 포함)
    @GetMapping("/classroom/{classroomId}/students-with-exam-info")
    public ResponseEntity<List<Map<String, Object>>> getStudentsWithExamInfoByClassroomId(@PathVariable Long classroomId) {
        List<Map<String, Object>> students = studentClassroomService.getStudentsWithExamInfoByClassroomId(classroomId);
        return ResponseEntity.ok(students);
    }


}
