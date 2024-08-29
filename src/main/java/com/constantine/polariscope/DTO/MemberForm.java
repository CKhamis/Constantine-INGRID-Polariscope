package com.constantine.polariscope.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberForm {
    private String id;
    private Integer ageMet;
    private LocalDate birthday;
    private String favoriteColor;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private String middleName;
    private String personality;
    private String description;
    private String relationship;
    private String sex;
    private String sexuality;

    private boolean archive;
    private boolean scoreHold;
}
