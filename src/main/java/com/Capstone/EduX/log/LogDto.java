package com.Capstone.EduX.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogDto {
    private final String timestamp;
    private final String logType;
    private final String detail;
}
