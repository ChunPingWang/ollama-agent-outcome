package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料聚合根。
 */
public class Profile {
    
    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    /**
     * 建立新的個人資料實例。
     *
     * @param id         識別碼，不可為 null
     * @param email      電子郵件值物件
     * @param displayName 顯示名稱值物件（可為 null）
     */
    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null"); // Email VO handles its own blank check, but explicit here for safety
        
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
     * 更新顯示名稱。回傳新的 Profile 實例以維持不可變性原則（或根據需求修改內部狀態）。
     * 此處採用返回新實例的方式以保持純度，若需效能優化可改為 mutable setter。
     * 
     * @param newName 新的顯示名稱
     * @return 包含新顯示名稱的 Profile 實例
     */
    public Profile changeDisplayName(DisplayName newName) {
        if (Objects.equals(this.displayName, newName)) {
            return this;
        }
        return new Profile(this.id, this.email, newName);
    }
}
