package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料 Domain Entity。
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    protected Profile(ProfileId id, Email email, DisplayName displayName) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        this.id = id;
        
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = email;
        
        this.displayName = displayName; // Can be null
    }

    /**
     * 靜態工廠方法建立新的 Profile。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
    }

    public ProfileId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null for update");
        }
        this.email = email;
    }

    public String getDisplayName() {
        return displayName != null ? displayName.getName() : null;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
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
