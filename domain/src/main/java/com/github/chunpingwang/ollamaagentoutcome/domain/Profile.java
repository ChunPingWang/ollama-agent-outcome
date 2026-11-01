package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料聚合根，封裝基本資訊與業務邏輯。
 */
public class Profile {

    private final UUID id;
    private Email email;
    private DisplayName displayName;

    public Profile(UUID id, Email email, DisplayName displayName) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        this.id = id;
        // Validation is handled by the Value Object constructors themselves
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = displayName; 
    }

    public ProfileId id() {
        return new ProfileId(id);
    }

    public Email email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱。
     * @param newName 新的顯示名稱值物件
     * @return 更新後的自身實例（可變設計以符合 JPA Entity 常見模式）
     */
    public Profile changeDisplayName(DisplayName newName) {
        this.displayName = newName;
        return this;
    }
}
