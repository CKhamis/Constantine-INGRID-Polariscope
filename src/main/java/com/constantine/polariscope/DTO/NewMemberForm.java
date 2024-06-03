package com.constantine.polariscope.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewMemberForm {
    private int ageMet;
    private LocalDate birthday;
    private String favoriteColor;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private String middleName;
    private String personality;
    private String relationship;
    private String sex;
    private String sexuality;
}
