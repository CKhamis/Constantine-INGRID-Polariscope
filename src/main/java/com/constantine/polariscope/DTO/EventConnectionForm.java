package com.constantine.polariscope.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventConnectionForm {
    @NotNull
    private UUID memberId;
    @NotNull
    private UUID eventId;
}
