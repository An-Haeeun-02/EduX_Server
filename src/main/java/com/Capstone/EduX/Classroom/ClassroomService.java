package com.Capstone.EduX.Classroom;

import com.Capstone.EduX.Classroom.dto.ClassroomCreateRequest;
import com.Capstone.EduX.Classroom.dto.ClassroomUpdateRequest;
import com.Capstone.EduX.professor.Professor;
import com.Capstone.EduX.professor.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ProfessorRepository professorRepository;

    public ClassroomService(ClassroomRepository classroomRepository, ProfessorRepository professorRepository) {  // 변수 이름 수정
        this.classroomRepository = classroomRepository;
        this.professorRepository = professorRepository;
    }
    // 강의실 생성 (입장코드 자동 생성)
    public Classroom createClassroom(ClassroomCreateRequest request) {
        Professor professor = professorRepository.findById(request.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 교수 정보를 찾을 수 없습니다."));

        Classroom classroom = new Classroom();
        classroom.setClassName(request.getClassName());
        classroom.setSection(request.getSection());
        classroom.setTime(request.getTime());
        classroom.setAccessCode(generateAccessCode());  // 입장코드 생성
        classroom.setProfessor(professor);  // 교수 설정

        return classroomRepository.save(classroom);
    }

    // 강의실 삭제
    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }

    // 강의실 수정 (이름, 분반, 시간만 수정 가능)
    public Classroom updateClassroom(Long id, ClassroomUpdateRequest request) {
        Optional<Classroom> optional = classroomRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("강의실을 찾을 수 없습니다: " + id);
        }

        Classroom classroom = optional.get();
        classroom.setClassName(request.getClassName());
        classroom.setSection(request.getSection());
        classroom.setTime(request.getTime());

        return classroomRepository.save(classroom);
    }

    // 랜덤 입장코드 생성 (6자리)
    private String generateAccessCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // 교수의 모든 강의실 조회 기능 추가
    public List<Classroom> getClassroomsByProfessorId(Long professorId) {
        return classroomRepository.findAllByProfessorId(professorId);
    }

    // 교수 정보 조회 기능 추가
    public Professor getProfessorInfo(Long professorId) {
        return professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교수 정보를 찾을 수 없습니다."));
    }

}
