package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體，包含基本資訊與業務行為。
 */
public final class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        // displayName can be null or empty as per spec logic in DisplayName constructor
        this.id = id;
        this.email = email;
        this.displayName = (displayName != null && displayName.name().isBlank()) ? new DisplayName("") : displayName;
    }

    /**
     * 靜態工廠方法建立新的個人資料。
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
    public void update(Email newEmail, DisplayName newDisplayName) {
        Objects.requireNonNull(newEmail, "new email must not be null");
        this.email = newEmail;
        if (newDisplayName != null && !newDisplayName.name().isBlank()) {
            this.displayName = newDisplayName;
        } else {
            this.displayName = new DisplayName("");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
