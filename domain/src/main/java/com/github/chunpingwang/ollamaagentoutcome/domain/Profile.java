package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體，包含基本資訊與業務行為。
 */
public final class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * 靜態工廠方法建立新的個人資料。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        if (id == null || email == null || displayName == null) {
            throw new IllegalArgumentException("all fields must be non-null");
        }
        return new Profile(id, email, displayName);
    }

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
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
        this.email = Objects.requireNonNull(email, "email cannot be null for update");
        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null for update");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile that)) return false;
        return Objects.equals(id, that.id) && 
               Objects.equals(email, that.email) && 
               Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, displayName);
    }
}
