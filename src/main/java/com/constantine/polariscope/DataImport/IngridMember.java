package com.constantine.polariscope.DataImport;

import com.constantine.polariscope.DTO.MemberForm;
import com.constantine.polariscope.Model.Evaluation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IngridMember extends MemberForm {
    List<Evaluation> timeline;
}
