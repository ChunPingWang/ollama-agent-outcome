package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Profile Domain Entity。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 靜態工廠方法建立新的 Profile。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        // Validation is handled by the Value Object constructors
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
     * 更新個人資料的電子郵件與顯示名稱。
     */
    public void update(Email email, DisplayName displayName) {
        this.email = email;
        this.displayName = displayName;
    }
}
