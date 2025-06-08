package com.Capstone.EduX.LoginSession;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionCleanupListener implements HttpSessionListener {

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        loginSessionRepository.deleteById(sessionId);
    }
}
