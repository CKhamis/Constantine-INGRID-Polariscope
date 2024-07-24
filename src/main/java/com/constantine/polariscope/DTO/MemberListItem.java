package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberListItem {
    private UUID id;
    private String firstName, lastName, middleName;
    private String sex, sexuality, relationship;
    private LocalDateTime created, lastModified;
    private Evaluation lastEvaluation;

    public MemberListItem(Member member){
        id = member.getId();
        firstName = member.getFirstName();
        lastName = member.getLastName();
        middleName = member.getMiddleName();
        sex = member.getSex().name();
        sexuality = member.getSexuality().name();
        relationship = member.getRelationship().name();
        created = member.getCreated();
        lastModified = member.getLastModified();
    }
}
