package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體，包含基本資訊與狀態管理邏輯。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "ID 不能為 null");
        Objects.requireNonNull(email, "電子郵件不能為 null");
        Objects.requireNonNull(displayName, "顯示名稱不能為 null");
        
        this.id = id;
        this.email = email;
        this.displayName = displayName;
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
     * 更新顯示名稱。會驗證新名稱的有效性。
     *
     * @param newName 新的顯示名稱值物件
     * @throws InvalidDisplayNameException 當新名稱為空或無效時
     */
    public void updateDisplayName(DisplayName newName) {
        // DisplayName constructor already validates non-blank and throws InvalidDisplayNameException
        if (newName == null || newName.name().isBlank()) {
            throw new InvalidDisplayNameException("顯示名稱不能為空");
        }
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
