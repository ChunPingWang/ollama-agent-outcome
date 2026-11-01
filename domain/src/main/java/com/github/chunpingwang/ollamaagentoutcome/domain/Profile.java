package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料聚合根，封裝基本業務邏輯與不變條件。
 */
public class Profile {

    private final UUID id;
    private final Email email;
    private DisplayName displayName;

    public Profile(UUID id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱，回傳新的 Profile 實例以維持不可變性設計。
     */
    public Profile changeDisplayName(DisplayName newName) {
        return new Profile(this.id, this.email, newName);
    }
}
