package com.Capstone.EduX.email;

import java.time.LocalDateTime;

public class VerificationCode {
    private final String code;
    private final LocalDateTime expiresAt;

    public VerificationCode(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public String getCode() {
        return code;
    }
}
