package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體。
 */
public final class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * 私有建構子，強制使用靜態工廠方法建立。
     */
    private Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 靜態工廠方法，建立新的個人資料。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
    }

    /**
     * 更新顯示名稱與電子郵件。
     */
    public void update(Email email, DisplayName displayName) {
        Objects.requireNonNull(email, "email must not be null for update");
        this.email = email;
        if (displayName != null) {
            this.displayName = displayName;
        }
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
