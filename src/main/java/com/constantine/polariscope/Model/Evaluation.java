package com.constantine.polariscope.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Evaluation {

    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime timestamp;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String note;
    private int cScore;

    @ManyToOne
    private Member member;

    public Evaluation(){
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }
}
