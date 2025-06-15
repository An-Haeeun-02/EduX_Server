package com.Capstone.EduX.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 여러 사용자 동시 인증을 위한 스레드 안전한 저장소
    private final Map<String, VerificationCode> codeStore = new ConcurrentHashMap<>();

    // 인증 코드 이메일 전송
    public void sendVerificationCode(String toEmail) {
        String code = createRandomCode();
        VerificationCode verificationCode = new VerificationCode(code, LocalDateTime.now().plusMinutes(5));
        codeStore.put(toEmail, verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[EduX] 이메일 인증 코드");
        message.setText("인증 코드는: " + code + " 입니다.\n5분 내에 입력해주세요.");
        mailSender.send(message);
    }

    // 인증 코드 확인
    public boolean verifyCode(String email, String inputCode) {
        VerificationCode saved = codeStore.get(email);
        if (saved == null || saved.isExpired()) {
            return false;
        }

        boolean match = saved.getCode().equals(inputCode);
        if (match) {
            codeStore.remove(email); // 성공하면 코드 제거
        }

        return match;
    }

    // 인증 코드 생성
    private String createRandomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
