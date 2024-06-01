package com.constantine.polariscope.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
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
    private LocalDate birthday;

    private LocalDateTime created;
    private LocalDateTime lastModified;

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
