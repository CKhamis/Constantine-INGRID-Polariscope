package com.constantine.polariscope.DTO;

import lombok.*;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class WebSpyResponse {
    boolean is_blocked;
    String message;
    String expire;
}