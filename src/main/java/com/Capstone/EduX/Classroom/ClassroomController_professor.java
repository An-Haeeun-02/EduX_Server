package com.Capstone.EduX.Classroom;

import com.Capstone.EduX.Classroom.dto.ClassroomCreateRequest;
import com.Capstone.EduX.Classroom.dto.ClassroomUpdateRequest;
import com.Capstone.EduX.professor.Professor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController_professor {

    private final ClassroomService classroomService;

    public ClassroomController_professor(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    // 강의실 생성
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody ClassroomCreateRequest request) {
        Classroom classroom = classroomService.createClassroom(request);
        return ResponseEntity.ok(classroom);
    }

    // 강의실 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok("강의실이 삭제되었습니다.");
    }

    // 강의실 수정
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long id, @RequestBody ClassroomUpdateRequest request) {
        Classroom updatedClassroom = classroomService.updateClassroom(id, request);
        return ResponseEntity.ok(updatedClassroom);
    }

    // 교수의 모든 강의실 조회 API
    @GetMapping("/professor/{professorId}/classrooms")
    public ResponseEntity<List<Classroom>> getClassroomsByProfessorId(@PathVariable Long professorId) {
        List<Classroom> classrooms = classroomService.getClassroomsByProfessorId(professorId);
        return ResponseEntity.ok(classrooms);
    }

    // 교수 정보 조회 API
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<Professor> getProfessorInfo(@PathVariable Long professorId) {
        Professor professor = classroomService.getProfessorInfo(professorId);
        return ResponseEntity.ok(professor);
    }

}
