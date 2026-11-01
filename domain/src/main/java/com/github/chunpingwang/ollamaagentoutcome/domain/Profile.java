package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile aggregate root containing core business logic validation.
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "ID cannot be null");
        this.id = id;
        setEmail(email); // Use setter to trigger validation in constructor context if needed, or direct assignment with validation
        setDisplayName(displayName);
    }

    /**
     * Creates a new profile instance for persistence conversion purposes.
     * This is typically used by the adapter layer when loading from DB.
     */
    protected Profile() {
        this.id = null; 
        this.email = null;
        this.displayName = null;
    }

    public ProfileId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        Objects.requireNonNull(email, "Email cannot be null during update");
        this.email = email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile that)) return false;
        return Objects.equals(id, that.id) &&
               Objects.equals(email, that.email) &&
               Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, displayName);
    }
}
