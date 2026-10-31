package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * JPA Entity：對應個人資料表格。
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class ProfileEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    private String bio;

    protected ProfileEntity() {
    }

    public ProfileEntity(UUID id, String email, String displayName, String bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
}
