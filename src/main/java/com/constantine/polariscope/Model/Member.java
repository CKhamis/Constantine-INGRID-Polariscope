package com.constantine.polariscope.Model;

import com.constantine.polariscope.DTO.MemberForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.ByteBuffer;
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
    @Column(columnDefinition="text")
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

    @ManyToOne
    private Place placeMet;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Evaluation> timeline;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "longblob")
    private byte[] profileImageData;
    private String profileImageType;
    private LocalDateTime profileImageTimestamp;

    @Transient
    private List<Evaluation> fullTimeline;

    @Transient
    private List<Evaluation> importedTimeline;

    @Transient
    private Evaluation mostRecentEval;

    @Transient
    private int timelineSize;

    public Member(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }

    public Member(@NonNull UUID id, @NonNull String firstName, @NonNull String lastName, String middleName, RelationshipType relationship, Sexuality sexuality, String personality, String favoriteColor, String description, Sex sex, Integer ageMet, LocalDate birthday, @NonNull LocalDateTime created, @NonNull LocalDateTime lastModified, @NonNull User author, Place placeMet, List<Evaluation> timeline) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.relationship = relationship;
        this.sexuality = sexuality;
        this.personality = personality;
        this.favoriteColor = favoriteColor;
        this.description = description;
        this.sex = sex;
        this.ageMet = ageMet;
        this.birthday = birthday;
        this.created = created;
        this.lastModified = lastModified;
        this.author = author;
        this.placeMet = placeMet;
        this.timeline = timeline;
    }

    public Member(MemberForm form, @NonNull User user){
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
        BISEXUAL,
        GAY,
        LESBIAN,
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
