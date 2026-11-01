package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料聚合根。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    public Profile(UUID id, String emailValue, String displayNameValue) {
        this.id = new ProfileId(id);
        this.email = new Email(emailValue);
        updateDisplayName(displayNameValue);
    }

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        this.id = id;
        this.email = email;
        // Ensure display name is never null in the object graph to prevent NPE on accessors if desired by tests
        this.displayName = (displayName == null || !displayName.isPresent()) ? new DisplayName((String) null) : displayName;
    }

    public ProfileId id() {
        return id;
    }

    public UUID getId() {
        return id().id();
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(String emailValue) {
        this.email = new Email(emailValue);
    }

    /**
     * 取得顯示名稱，若未設定則回傳空的 DisplayName 物件以避免 NullPointer。
     */
    public DisplayName getDisplayName() {
        return displayName != null ? displayName : new DisplayName((String) null);
    }

    /**
     * 更新顯示名稱，若為空白則視為未設定（null）。
     */
    public void updateDisplayName(String value) {
        this.displayName = (value != null && !value.isBlank()) ? new DisplayName(value) : new DisplayName((String) null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
