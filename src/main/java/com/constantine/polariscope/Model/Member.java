package com.constantine.polariscope.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private RelationshipType relationship;
    private Sexuality sexuality;
    private String personality;
    private String favoriteColor;
    private String description;
    private Sex sex;
    private int ageMet;
    private String placeMet; //todo: make this its own table
    private LocalDate birthday;

    @OneToMany(mappedBy = "member")
    List<Evaluation> timeline;

    private enum Sex{
        Male,
        Female,
        Other,
    }
    private enum Sexuality{
        HETEROSEXUAL,
        PANSEXUAL,
        BISEXUAL,
        HOMOSEXUAL,
        ASEXUAL,
    }

    private enum RelationshipType{
        FAMILY,
        COWORKER,
        FRIEND,
        ACQUAINTANCE,
        ENEMY,
        PARTNER,
    }
}
