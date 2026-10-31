package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Profile Domain Entity representing a user's personal information.
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    public ProfileId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    /**
     * Updates the email address of the profile.
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    /**
     * Updates the display name of the profile.
     */
    public void updateDisplayName(DisplayName newDisplayName) {
        this.displayName = newDisplayName;
    }
}
