package com.constantine.polariscope.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventForm {
    private UUID id;
    private String label;
    private String description;
    private LocalDateTime date;
    @Min(value = 0, message = "Value must be at least 0")
    @Max(value = 255, message = "Value must be at most 255")
    private int red;
    @Min(value = 0, message = "Value must be at least 0")
    @Max(value = 255, message = "Value must be at most 255")
    private int green;
    @Min(value = 0, message = "Value must be at least 0")
    @Max(value = 255, message = "Value must be at most 255")
    private int blue;
}
