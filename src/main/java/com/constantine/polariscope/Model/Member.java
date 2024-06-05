package com.constantine.polariscope.Model;

import com.constantine.polariscope.DTO.NewMemberForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Member {
    @Id
    @NonNull
    @GeneratedValue
    private UUID id;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    private String middleName;
    private RelationshipType relationship;
    private Sexuality sexuality;
    private String personality;
    private String favoriteColor;
    private String description;
    private Sex sex;
    private Integer ageMet;
    private LocalDate birthday;

    @NonNull
    private LocalDateTime created;
    @NonNull
    private LocalDateTime lastModified;

    @JsonIgnore
    @NonNull
    @ManyToOne
    private User author;

    @OneToOne
    private Place placeMet;

    @OneToMany(mappedBy = "member")
    List<Evaluation> timeline;

    public Member(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }

    public Member(NewMemberForm form, @NonNull User user){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
        ageMet = form.getAgeMet();
        birthday = form.getBirthday();
        favoriteColor = form.getFavoriteColor();
        firstName = form.getFirstName();
        middleName = form.getMiddleName();
        lastName = form.getLastName();
        personality = form.getPersonality();
        description = form.getDescription();

        if(form.getRelationship() != null && !form.getRelationship().isEmpty()){
            relationship = RelationshipType.valueOf(form.getRelationship().toUpperCase());
        }
        if(form.getSex() != null && !form.getSex().isEmpty()){
            sex = Sex.valueOf(form.getSex().toUpperCase());
        }
        if(form.getSexuality() != null && !form.getSexuality().isEmpty()){
            sexuality = Sexuality.valueOf(form.getSexuality().toUpperCase());
        }

        author = user;
    }
    public enum Sex{
        MALE,
        FEMALE,
        OTHER,
    }
    public enum Sexuality{
        HETEROSEXUAL,
        PANSEXUAL,
        BISEXUAL,
        HOMOSEXUAL,
        ASEXUAL,
    }

    public enum RelationshipType{
        FAMILY,
        COWORKER,
        FRIEND,
        ACQUAINTANCE,
        ENEMY,
        PARTNER,
        INACTIVE,
    }
}
