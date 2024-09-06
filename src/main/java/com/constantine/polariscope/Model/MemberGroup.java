package com.constantine.polariscope.Model;

import com.constantine.polariscope.DTO.GroupForm;
import com.constantine.polariscope.Util.EncryptUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class MemberGroup {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private Color color;
    private LocalDateTime created;
    private LocalDateTime lastModified;

    @JsonIgnore
    @ManyToOne
    private User author;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "longblob")
    private byte[] profileImageData;
    private String profileImageType;
    private LocalDateTime profileImageTimestamp;

    public MemberGroup(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }

    public MemberGroup(GroupForm form, User author){
        this.name = form.getName();
        this.description = form.getDescription();
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.author = author;
    }

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.lastModified = LocalDateTime.now(); //not encrypted so I can order by last modified in query
        this.name = EncryptUtil.encryptString(this.name, this.id);
        this.description = EncryptUtil.encryptString(this.description, this.id);
        this.created = EncryptUtil.encryptDateTime(this.created, this.id);

        this.profileImageData = EncryptUtil.encryptBytes(this.profileImageData, this.id);
        this.profileImageType = EncryptUtil.encryptString(this.profileImageType, this.id);
        this.profileImageTimestamp = EncryptUtil.encryptDateTime(this.profileImageTimestamp, this.id);
    }

    @PostLoad
    private void decrypt() {
        this.name = EncryptUtil.decryptString(this.name, this.id);
        this.description = EncryptUtil.decryptString(this.description, this.id);
        this.created = EncryptUtil.decryptDateTime(this.created, this.id);

        this.profileImageData = EncryptUtil.decryptBytes(this.profileImageData, this.id);
        this.profileImageType = EncryptUtil.decryptString(this.profileImageType, this.id);
        this.profileImageTimestamp = EncryptUtil.decryptDateTime(this.profileImageTimestamp, this.id);
    }
}
