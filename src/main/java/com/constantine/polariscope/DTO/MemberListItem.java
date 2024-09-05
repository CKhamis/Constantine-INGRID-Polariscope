package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.MemberGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private boolean scoreHold;
    private boolean archive;
    private LocalDate birthday;
    private MemberGroup group;

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
        scoreHold = member.isScoreHold();
        archive = member.isArchive();
        birthday = member.getBirthday();
        group = member.getGroup();
    }
}
