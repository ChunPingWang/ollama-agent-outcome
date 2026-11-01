package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料 Domain Entity。
 */
public final class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }

    /**
     * 靜態工廠方法建立新的 Profile。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        // Validate ID format again to ensure consistency if passed directly
        new ProfileId(id.value()); 
        return new Profile(id, email, displayName);
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

    /**
     * 更新電子郵件與顯示名稱。
     */
    public void update(Email email, DisplayName displayName) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }
}
