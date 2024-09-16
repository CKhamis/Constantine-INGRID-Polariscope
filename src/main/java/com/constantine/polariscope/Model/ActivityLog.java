package com.constantine.polariscope.Model;

import com.constantine.polariscope.Service.ActivityLogService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime modified;
    @NonNull
    private ActivityType activityType;

    @JsonIgnore
    @ManyToOne
    private User user;

    @PreUpdate
    private void save() {
        this.modified = LocalDateTime.now();
    }

    @PrePersist
    private void create(){
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public ActivityLog(@NonNull ActivityType type, User user){
        this.activityType = type;
        this.user = user;
    }

    public enum ActivityType{
        MEMBER_CREATED,
        MEMBER_MODIFIED,
        MEMBER_DELETED,
        EVALUATION_CREATED,
        EVALUATION_MODIFIED,
        EVALUATION_DELETED,

    }
}
