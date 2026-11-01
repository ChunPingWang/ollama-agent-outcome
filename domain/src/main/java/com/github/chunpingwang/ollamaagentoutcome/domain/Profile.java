package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體。
 */
public final class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "識別碼不能為 null");
        this.email = Objects.requireNonNull(email, "電子郵件不能為 null");
        this.displayName = displayName; // 允許 null
    }

    /**
     * 靜態工廠方法，建立新的個人資料實體。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
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
     * 更新顯示名稱與電子郵件。
     */
    public void update(Email email, DisplayName displayName) {
        this.email = Objects.requireNonNull(email, "電子郵件不能為 null");
        this.displayName = displayName;
    }
}
