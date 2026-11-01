package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料實體，包含核心業務邏輯與狀態管理。
 */
public final class Profile {

    private final String id;
    private Email email;
    private DisplayName displayName;

    public Profile(String id, Email email, DisplayName displayName) {
        if (id == null || id.isBlank()) {
            throw new NullPointerException("識別碼不能為空");
        }
        try {
            new ProfileId(id);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("無效的識別碼格式", e);
        }
        this.id = id;
        this.email = Objects.requireNonNull(email, "電子郵件不能為空");
        this.displayName = Objects.requireNonNull(displayName, "顯示名稱不能為空");
    }

    public String id() {
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
    public Profile changeDisplayName(DisplayName newName) {
        // DisplayName constructor handles blank check
        return new Profile(this.id, this.email, newName);
    }

    /**
     * 更新電子郵件地址。
     */
    public Profile changeEmail(Email newEmail) {
        // Email constructor handles format validation
        return new Profile(this.id, newEmail, this.displayName);
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
