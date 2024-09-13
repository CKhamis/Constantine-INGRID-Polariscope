package com.constantine.polariscope.Model;

import com.constantine.polariscope.Util.EncryptUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue
    private UUID id;
    private String label;
    private LocalDate date;

    private Color color;
    private LocalDateTime created;
    private LocalDateTime modified;

    @JsonIgnore
    @ManyToOne
    private User author;

    @ManyToMany
    private ArrayList<Member> members;

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.modified = LocalDateTime.now();
        this.label = EncryptUtil.encryptString(this.label, this.id);
        this.date = EncryptUtil.encryptDate(this.date, this.id);
        this.created = EncryptUtil.encryptDateTime(this.created, this.id);
    }

    @PostLoad
    private void decrypt() {
        this.label = EncryptUtil.decryptString(this.label, this.id);
        this.date = EncryptUtil.decryptDate(this.date, this.id);
        this.created = EncryptUtil.decryptDateTime(this.created, this.id);
    }
}
