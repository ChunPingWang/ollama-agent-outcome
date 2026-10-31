package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Domain Entity representing a user's personal profile.
 */
public final class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.displayName = displayName != null ? new DisplayName(displayName.name) : new DisplayName(null);
    }

    /**
     * Static factory method to create a new Profile instance.
     */
    public static Profile create(ProfileId id, String emailStr, String displayNameStr) {
        return new Profile(id, new Email(emailStr), new DisplayName(displayNameStr));
    }

    /**
     * Updates the display name and optionally the email address of the profile.
     */
    public void update(String newDisplayName, String newEmail) {
        if (newEmail != null && !newEmail.isBlank()) {
            // Validate format before updating
            try {
                new Email(newEmail);
            } catch (InvalidEmailFormatException e) {
                throw new InvalidEmailFormatException("New email is invalid: " + newEmail);
            }
            this.email = new Email(newEmail);
        }
        
        if (newDisplayName != null) {
            this.displayName = new DisplayName(newDisplayName);
        } else {
            this.displayName = new DisplayName(null);
        }
    }

    public ProfileId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }
}
