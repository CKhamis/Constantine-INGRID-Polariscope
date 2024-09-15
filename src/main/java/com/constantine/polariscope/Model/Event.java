package com.constantine.polariscope.Model;

import com.constantine.polariscope.DTO.EventForm;
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
import java.util.HashSet;
import java.util.Set;
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
    private LocalDateTime date;
    private String description;

    private Color color;
    private LocalDateTime created;
    private LocalDateTime modified;

    @JsonIgnore
    @ManyToOne
    private User author;

    @ManyToMany
    private Set<Event> members = new HashSet<>();

    public Event(User author, EventForm form){
        this.author = author;
        this.label = form.getLabel();
        this.date = form.getDate();
        this.description = form.getDescription();
        this.color = new Color(form.getRed(), form.getGreen(), form.getBlue());
        this.created = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void encrypt() {
        this.modified = LocalDateTime.now();
        this.label = EncryptUtil.encryptString(this.label, this.id);
        this.date = EncryptUtil.encryptDateTime(this.date, this.id);
        this.created = EncryptUtil.encryptDateTime(this.created, this.id);
        this.description = EncryptUtil.encryptString(this.description, this.id);
    }

    @PostLoad
    private void decrypt() {
        this.label = EncryptUtil.decryptString(this.label, this.id);
        this.date = EncryptUtil.decryptDateTime(this.date, this.id);
        this.created = EncryptUtil.decryptDateTime(this.created, this.id);
        this.description = EncryptUtil.decryptString(this.description, this.id);
    }
}
