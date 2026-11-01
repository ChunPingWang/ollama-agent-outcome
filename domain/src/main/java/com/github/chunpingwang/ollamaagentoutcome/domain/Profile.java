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
        this.email = email;
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return Objects.equals(id, profile.id) && 
               Objects.equals(email, profile.email) && 
               Objects.equals(displayName, profile.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, displayName);
    }
}
