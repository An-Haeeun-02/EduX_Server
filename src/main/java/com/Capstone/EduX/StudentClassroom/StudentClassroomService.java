package com.Capstone.EduX.StudentClassroom;

import com.Capstone.EduX.Classroom.Classroom;
import com.Capstone.EduX.Classroom.ClassroomRepository;
import com.Capstone.EduX.student.Student;
import com.Capstone.EduX.student.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentClassroomService {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentClassroomRepository studentClassroomRepository;

    public StudentClassroomService(StudentRepository studentRepository,
                                   ClassroomRepository classroomRepository,
                                   StudentClassroomRepository studentClassroomRepository) {
        this.studentRepository = studentRepository;
        this.classroomRepository = classroomRepository;
        this.studentClassroomRepository = studentClassroomRepository;
    }

    public List<Classroom> getClassrooms(String studentId) {
        return studentClassroomRepository.findClassroomsByStudentId(studentId);
    }

    public String joinClassroom(String studentId, String accessCode) {
        // 1. 강의실 찾기
        Classroom classroom = classroomRepository.findByAccessCode(accessCode);
        if (classroom == null) {
            throw new IllegalArgumentException("잘못된 코드입니다.");
        }

        // 2. 학생 찾기
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new NoSuchElementException("학생을 찾을 수 없습니다.");
        }

        // 3. 이미 가입했는지 확인
        boolean exists = studentClassroomRepository.existsByStudentAndClassroom(student, classroom);
        if (exists) {
            throw new IllegalStateException("이미 참여한 강의실입니다.");
        }

        // 4. 참여 저장
        StudentClassroom sc = new StudentClassroom();
        sc.setStudent(student);
        sc.setClassroom(classroom);
        sc.setConnected(true);
        studentClassroomRepository.save(sc);

        return classroom.getClassName();
    }
}
