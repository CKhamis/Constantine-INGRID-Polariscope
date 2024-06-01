package com.constantine.polariscope.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    public Place(){
        created = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }
}
