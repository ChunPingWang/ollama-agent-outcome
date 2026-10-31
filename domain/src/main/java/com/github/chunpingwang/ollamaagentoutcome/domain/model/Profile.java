package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;

/**
 * Profile entity representing user's personal information.
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;
    private Bio bio;

    public Profile(ProfileId id, Email email, DisplayName displayName, Bio bio) {
        this.id = id;
        update(email, displayName, bio);
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
     * Updates the profile with new values after validation.
     */
    public void update(Email email, DisplayName displayName, Bio bio) {
        // Validation happens in VO constructors, so if we reach here, they are valid.
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }
}
