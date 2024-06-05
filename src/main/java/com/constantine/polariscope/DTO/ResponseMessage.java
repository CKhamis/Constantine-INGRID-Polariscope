package com.constantine.polariscope.DTO;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseMessage {
    private final LocalDateTime timestamp;
    private final Severity severity;
    private final String message;
    private final String title;

    public ResponseMessage(String title, Severity severity, String message) {
        timestamp = LocalDateTime.now();
        this.severity = severity;
        this.message = message;
        this.title = title;
    }

    public enum Severity {
        INFORMATIONAL,
        LOW,
        MEDIUM,
        HIGH,
        INSANE
    }
}
