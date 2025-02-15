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
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Relationship {
    @Id
    @NonNull
    @GeneratedValue
    private UUID id;

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

    private Member.RelationshipType type;

    @NotNull
    private LocalDateTime created;
    @NotNull
    private LocalDateTime lastModified;

    public Relationship() {
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.lastModified = LocalDateTime.now();
        this.health = EncryptUtil.encryptInteger(this.health, this.id);
    }

    @PostLoad
    private void decrypt() {
        this.health = EncryptUtil.decryptInteger(this.health, this.id);
    }
}
