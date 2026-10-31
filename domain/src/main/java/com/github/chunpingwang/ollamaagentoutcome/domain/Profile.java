package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料 Domain Entity。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 靜態工廠方法，建立新的個人資料實體。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
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
     * 更新顯示名稱。
     */
    public void updateDisplayName(DisplayName newName) {
        this.displayName = newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
