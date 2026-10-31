package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.springframework.data.domain.Persistable;

import java.time.Instant;

/**
 * JPA Entity mapping for Profile domain object.
 * Implements Persistable to handle new vs existing state correctly with UUIDs.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private String bio;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Tracks whether the entity is newly created and not yet persisted.
     */
    @Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // For JPA instantiation only
    }

    public ProfileEntity(String id, String email, String displayName, String bio, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isNew = true;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Marks the entity as not new after persistence or loading.
     */
    void markNotNew() {
        this.isNew = false;
    }

    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    private void onPostLoadOrPersist() {
        this.isNew = false;
    }
}
