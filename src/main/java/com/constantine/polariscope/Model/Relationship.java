package com.constantine.polariscope.Model;

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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Relationship {
    @Id
    @NotNull
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

    @NotNull
    private LocalDateTime created;
    @NotNull
    private LocalDateTime lastModified;
}
