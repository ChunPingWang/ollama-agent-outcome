package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料領域實體。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    /**
     * 建立新的 Profile。
     *
     * @param id         識別碼
     * @param email      電子郵件
     * @param displayName 顯示名稱
     */
    public Profile(UUID id, Email email, DisplayName displayName) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        this.id = new ProfileId(id.toString());
        this.email = email;
        this.displayName = displayName;
    }

    public ProfileId id() {
        return id;
    }

    public UUID getIdAsUUID() {
        return java.util.UUID.fromString(id.value());
    }

    public Email email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱。若傳入 null，則將顯示名稱設為 null。
     *
     * @param newName 新的顯示名稱值物件（可為 null）
     */
    public void updateDisplayName(DisplayName newName) {
        // 如果 newName 是 null，直接設定為 null；否則使用其內容
        // 注意：測試中呼叫 profile.updateDisplayName(null)，這意味著參數本身可以是 null
        // 但為了符合 record 的不可變性與一致性，我們允許外部傳入 null 來清除狀態
        if (newName != null) {
             this.displayName = newName;
        } else {
            this.displayName = null;
        }
    }
}
