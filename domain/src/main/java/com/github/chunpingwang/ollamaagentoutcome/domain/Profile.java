package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料聚合根，封裝基本業務邏輯。
 */
public class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        
        this.id = id;
        this.email = email;
        // Store reference directly or copy? Record is immutable, so just assign.
        // Note: We need to allow changing display name later, but keep it encapsulated.
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
     * 更新顯示名稱。回傳新的 Profile 實例以維持不可變性原則（或內部狀態變更）。
     * 此處採用內部狀態變更方式以便於 JPA Entity 轉換與管理，但對外提供新參考。
     */
    public Profile changeDisplayName(DisplayName newName) {
        if (newName == null || newName.value() == null || newName.value().isBlank()) {
            throw new IllegalArgumentException("Display name cannot be blank");
        }
        this.displayName = newName;
        return this;
    }
}
