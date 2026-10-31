package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(displayName, "displayName cannot be null");
        
        this.id = id;
        this.email = email;
        this.displayName = displayName;
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

    /**
     * 更新顯示名稱。
     */
    public void updateDisplayName(DisplayName newName) {
        Objects.requireNonNull(newName);
        this.displayName = newName;
    }
}
