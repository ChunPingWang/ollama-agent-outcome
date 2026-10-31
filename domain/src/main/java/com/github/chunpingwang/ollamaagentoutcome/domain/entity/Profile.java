package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Profile 領域實體（Aggregate Root）。
 */
public class Profile {

    private ProfileId id;
    private Email email;
    private DisplayName displayName;
    private Bio bio;

    protected Profile() {
        // JPA / Testcontainers 需要無參數建構子，但業務邏輯應使用有參建構子
    }

    public Profile(ProfileId id, Email email, DisplayName displayName, Bio bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName != null ? displayName : new DisplayName(null);
        this.bio = bio != null ? bio : new Bio("");
    }

    public void changeDisplayName(DisplayName newName) {
        if (newName == null || newName.name().isBlank()) {
            throw new IllegalArgumentException("Display name cannot be blank");
        }
        this.displayName = newName;
    }

    public void changeBio(Bio newBio) {
        this.bio = newBio;
    }

    /**
     * 變更電子郵件地址。
     *
     * @param newEmail 新的電子郵件值對象
     */
    public void changeEmail(Email newEmail) {
        this.email = newEmail;
    }

    public ProfileId getId() { return id; }
    public Email getEmail() { return email; }
    public DisplayName getDisplayName() { return displayName; }
    public Bio getBio() { return bio; }
}
