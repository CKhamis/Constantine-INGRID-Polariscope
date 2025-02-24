package com.constantine.polariscope.Model;

import com.constantine.polariscope.Util.EncryptUtil;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Defines the relationships between two members
 */
@Getter
@Setter
@Entity
public class Relationship {
    @Id
    @NotNull
    private String id;

    @NotNull
    @ManyToOne
    private User author;
    @NotNull
    @ManyToOne
    private Member self;

    @NotNull
    @ManyToOne
    private Member other;

    @NotNull
    private int health;

    @Column(columnDefinition="text")
    private String description;

    private Member.RelationshipType type;

    @NotNull
    private LocalDateTime created;
    @NotNull
    private LocalDateTime lastModified;

    public Relationship(User author, Member self, Member other, int health, String description, Member.RelationshipType type) {
        this.id = self.getId().toString() + other.getId().toString();
        this.author = author;
        this.self = self;
        this.other = other;
        this.description = description;
        this.health = health;
        this.type = type;
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public Relationship() {
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.lastModified = LocalDateTime.now();
        this.health = EncryptUtil.encryptInteger(this.health, this.self.getId());
    }

    @PostLoad
    private void decrypt() {
        this.health = EncryptUtil.decryptInteger(this.health, this.self.getId());
    }
}
