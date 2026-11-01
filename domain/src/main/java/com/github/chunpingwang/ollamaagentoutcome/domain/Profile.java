package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料 Domain Entity。ID、Email 不可變，DisplayName 可更新。
 */
public final class Profile {

    private final UUID id;
    private final Email email;
    private DisplayName displayName;

    public Profile(UUID id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id 不能為 null");
        Objects.requireNonNull(email, "email 不能為 null");
        // displayName 允許 null
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 取得識別碼。
     */
    public UUID id() {
        return id;
    }

    /**
     * 取得電子郵件。
     */
    public Email email() {
        return email;
    }

    /**
     * 取得顯示名稱。
     */
    public DisplayName displayName() {
        return displayName;
    }

    /**
     * 變更顯示名稱。若傳入 null 則設為 null。
     */
    public void changeDisplayName(DisplayName newDisplayName) {
        this.displayName = newDisplayName;
    }
}
