package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Profile entity representing the aggregate root for personal profile data.
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * Constructs a new Profile with the given ID, email and display name.
     *
     * @param id          the unique identifier for this profile
     * @param email       the validated email address value object
     * @param displayName the validated display name value object
     */
    public Profile(ProfileId id, Email email, DisplayName displayName) {
        if (id == null || email == null || displayName == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * Returns the unique identifier of this profile.
     *
     * @return the profile ID
     */
    public ProfileId id() {
        return id;
    }

    /**
     * Returns the current email address.
     *
     * @return the email value object
     */
    public Email email() {
        return email;
    }

    /**
     * Returns the current display name.
     *
     * @return the display name value object
     */
    public DisplayName displayName() {
        return displayName;
    }

    /**
     * Updates the email and display name with validation.
     *
     * @param newEmail      the new validated email address
     * @param newDisplayName the new validated display name
     */
    public void update(Email newEmail, DisplayName newDisplayName) {
        if (newEmail == null || newDisplayName == null) {
            throw new IllegalArgumentException("New values must not be null");
        }
        this.email = newEmail;
        this.displayName = newDisplayName;
    }
}
