package com.constantine.polariscope.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User implements UserDetails {
    //todo: profile icon?
    @Id
    @GeneratedValue
    private UUID id;

    @NonNull
    private String username;
    @JsonIgnore
    @NonNull
    private String password;
    @NonNull
    private Role role;
    private boolean isEnabled;
    @NonNull
    private LocalDateTime created;
    @NonNull
    private LocalDateTime modified;
    private boolean tutorialNeeded;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Member> members;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<MemberGroup> groups;

    @PrePersist
    protected void onCreate(){
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        tutorialNeeded = true;
    }

    @PreUpdate
    protected void onUpdate(){
        this.modified = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority= new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public @NonNull String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public enum Role{
        Owner,
        ADMIN,
        Observer
    }
}
