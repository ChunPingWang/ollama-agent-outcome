package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import java.util.Objects;

/**
 * Profile entity representing a user's personal information.
 */
public final class Profile {
    
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;
    private Bio bio;

    /**
     * Creates a new profile with the given attributes.
     *
     * @param id unique identifier
     * @param email valid email address
     * @param displayName display name (can be null or empty)
     * @param bio biography content (can be null or empty)
     */
    public Profile(ProfileId id, Email email, DisplayName displayName, Bio bio) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.displayName = Objects.requireNonNull(displayName);
        this.bio = Objects.requireNonNull(bio);
    }

    public ProfileId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public Bio bio() {
        return bio;
    }

    /**
     * Updates the display name and bio of this profile.
     *
     * @param newName the new display name
     * @param newBio  the new bio content
     */
    public void updateDisplayNameAndBio(DisplayName newName, Bio newBio) {
        this.displayName = Objects.requireNonNull(newName);
        this.bio = Objects.requireNonNull(newBio);
    }
}
