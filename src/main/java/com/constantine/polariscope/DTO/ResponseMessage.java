package com.constantine.polariscope.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseMessage {
    private LocalDateTime timestamp;
    private Severity severity;
    private String message;
    private String title;

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
