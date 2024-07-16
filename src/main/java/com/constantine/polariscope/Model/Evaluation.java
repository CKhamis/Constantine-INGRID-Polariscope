package com.constantine.polariscope.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    @Column(columnDefinition="text")
    private String note;
    private Integer cScore;

    @JsonIgnore
    @ManyToOne
    private Member member;

    public Evaluation(){
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }

    public Evaluation(UUID id, LocalDateTime timestamp, String note, Integer cScore, Member member) {
        this.id = id;
        this.timestamp = timestamp;
        this.note = note;
        this.cScore = cScore;
        this.member = member;
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }
}
