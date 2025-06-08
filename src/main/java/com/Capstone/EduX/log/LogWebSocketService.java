package com.Capstone.EduX.log;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendExamLog(Long examId, Map<String, Object> message) {
        String destination = "/topic/exam/" + examId; // 프론트에서 구독할 주소
        messagingTemplate.convertAndSend(destination, message);
    }
}
