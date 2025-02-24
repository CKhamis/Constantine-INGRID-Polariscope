package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.Member;
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
public class MemberRelationshipForm {
    @NotNull
    private UUID self;
    @NotNull
    private UUID other;
    private Integer health;
    @NotNull
    private Member.RelationshipType type;
    private String description;

}
