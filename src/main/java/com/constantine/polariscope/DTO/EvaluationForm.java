package com.constantine.polariscope.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationForm {
    private UUID id;
    private String note;
    private Integer cscore;
    @NonNull
    private LocalDateTime timestamp;
    @NonNull
    private UUID memberId;
}
