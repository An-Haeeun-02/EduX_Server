package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.examInfo.ExamInfo;
import com.Capstone.EduX.examInfo.ExamInfoService;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
    @RequestMapping("/api/student-classrooms")
    public class StudentClassroomController {

        private final StudentClassroomService studentClassroomService;
        private final ExamInfoService examInfoService;
        private final ClassroomRepository classroomRepository;
        private final StudentRepository studentRepository;
        private final StudentClassroomRepository studentClassroomRepository;


        public StudentClassroomController(StudentClassroomService studentClassroomService, ExamInfoService examInfoService, ClassroomRepository classroomRepository, StudentRepository studentRepository, StudentClassroomRepository studentClassroomRepository) {
            this.studentClassroomService = studentClassroomService;
            this.examInfoService = examInfoService;
            this.classroomRepository = classroomRepository;
            this.studentRepository = studentRepository;
            this.studentClassroomRepository = studentClassroomRepository;
        }

        //소속된 강의실 가져오기
        @GetMapping("/{studentId}")
        public ResponseEntity<List<Map<String, Object>>> getClassrooms(@PathVariable String studentId) {
            List<Classroom> classrooms = studentClassroomService.getClassrooms(studentId);

            List<Map<String, Object>> result = classrooms.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("className", c.getClassName());
                return map;
            }).toList();

            return ResponseEntity.ok(result);
        }

        @GetMapping("/active-exams/{classroomId}")
        public ResponseEntity<List<Map<String, Object>>> getActiveExams(@PathVariable Long classroomId) {
            List<ExamInfo> exams = examInfoService.getActiveExams(classroomId);

            List<Map<String, Object>> result = exams.stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", e.getId());
                map.put("title", e.getTitle());
                return map;
            }).toList();

            return ResponseEntity.ok(result);
        }

    @PostMapping("/join-classroom")
    public ResponseEntity<?> joinClassroom(@RequestBody Map<String, String> request) {
        String accessCode = request.get("accessCode");
        String studentId = request.get("studentId");

        // 1. 강의실 찾기
        Classroom classroom = classroomRepository.findByAccessCode(accessCode);
        if (classroom == null) {
            return ResponseEntity.status(400).body("잘못된 코드입니다.");
        }

        // 2. 학생 찾기
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            return ResponseEntity.status(404).body("학생을 찾을 수 없습니다.");
        }

        // 3. 이미 참여 중인지 확인
        boolean alreadyJoined = studentClassroomRepository.existsByStudentAndClassroom(student, classroom);
        if (alreadyJoined) {
            return ResponseEntity.status(409).body("이미 참여한 강의실입니다.");
        }

        // 4. 관계 생성
        StudentClassroom sc = new StudentClassroom();
        sc.setStudent(student);
        sc.setClassroom(classroom);
        sc.setConnected(true); // isConnected = true
        studentClassroomRepository.save(sc);

        // 5. className 전달
        Map<String, String> result = new HashMap<>();
        result.put("className", classroom.getClassName());
        return ResponseEntity.ok(result);
    }


}

