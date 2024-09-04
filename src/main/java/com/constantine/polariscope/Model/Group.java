package com.constantine.polariscope.Model;

import com.constantine.polariscope.Util.EncryptUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Group {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime created;
    private LocalDateTime lastModified;

    @JsonIgnore
    @ManyToOne
    private User author;

    public Group(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.lastModified = LocalDateTime.now(); //not encrypted so I can order by last modified in query
        this.name = EncryptUtil.encryptString(this.name, this.id);
        this.description = EncryptUtil.encryptString(this.description, this.id);
        this.created = EncryptUtil.encryptDateTime(this.created, this.id);
    }

    @PostLoad
    private void decrypt() {
        this.name = EncryptUtil.decryptString(this.name, this.id);
        this.description = EncryptUtil.decryptString(this.description, this.id);
        this.created = EncryptUtil.decryptDateTime(this.created, this.id);
    }
}
