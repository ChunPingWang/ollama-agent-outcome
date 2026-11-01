package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain entity representing a user's personal profile.
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * Static factory method to create a new profile instance.
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
    }

    /**
     * Updates the email and display name of the profile.
     */
    public void update(Email email, DisplayName displayName) {
        this.email = email;
        this.displayName = displayName;
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
}
