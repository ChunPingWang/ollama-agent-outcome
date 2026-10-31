package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Profile Aggregate Root, responsible for business logic and state management.
 */
public class Profile {

    private final UUID id;
    private final Email email;
    private DisplayName displayName;
    private Bio bio;
    private final Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Tracks whether the profile is newly created and not yet persisted.
     */
    private transient boolean isNew = true;

    protected Profile() {
        // For JPA instantiation only
        this.id = null;
        this.email = null;
        this.displayName = null;
        this.bio = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.isNew = false;
    }

    public Profile(UUID id, Email email, DisplayName displayName, Bio bio) {
        if (id == null || id.toString().isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.isNew = true;
    }

    /**
     * Updates display name and bio. Email cannot be changed directly here.
     */
    public void update(DisplayName newDisplayName, Bio newBio) {
        this.displayName = newDisplayName;
        this.bio = newBio;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public Bio getBio() {
        return bio;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Profile{id=" + id + ", email=" + (email != null ? email.address() : null) + "}";
    }

    // --- Persistable Support Methods ---

    /**
     * Checks if the profile is newly created and not yet persisted.
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Marks this profile as no longer new (typically called by JPA lifecycle or converter).
     */
    public void markNotNew() {
        this.isNew = false;
    }
}
