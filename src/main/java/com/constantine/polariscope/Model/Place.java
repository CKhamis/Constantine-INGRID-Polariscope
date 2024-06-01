package com.constantine.polariscope.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Place {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private LocalDateTime created;
    private LocalDateTime lastModified;

    @ManyToOne
    private User author;

    public Place(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }
}
