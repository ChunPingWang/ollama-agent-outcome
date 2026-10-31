package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity for Profile persistence using UUID as primary key.
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String displayName;

    private String bio;

    // For Persistable support with assigned IDs
    private boolean isNew = true;

    public ProfileEntity() {}

    public ProfileEntity(UUID id, String email, String displayName, String bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.isNew = true;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
