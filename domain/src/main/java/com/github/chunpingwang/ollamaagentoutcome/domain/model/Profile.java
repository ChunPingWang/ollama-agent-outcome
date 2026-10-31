package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 個人資料領域模型。
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;
    private Bio bio;

    public Profile(ProfileId id, Email email, DisplayName displayName, Bio bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    public ProfileId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public void updateDisplayName(String newName) {
        this.displayName = new DisplayName(newName);
    }

    public java.util.Optional<Bio> bio() {
        return java.util.Optional.ofNullable(bio);
    }

    public void updateBio(String newBio) {
        if (newBio == null || newBio.isEmpty()) {
            this.bio = null;
        } else {
            this.bio = new Bio(newBio);
        }
    }
}
