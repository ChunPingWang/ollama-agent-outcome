package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * 個人資料聚合根。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;
    private Instant createdAt;

    /**
     * 建立新的個人資料。
     *
     * @param id          唯一識別碼
     * @param email       電子郵件地址
     * @param displayName 顯示名稱
     */
    public Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        
        if (displayName != null && !displayName.value().isBlank()) {
            this.displayName = new DisplayName(displayName.value());
        } else {
            this.displayName = new DisplayName("");
        }
        
        this.createdAt = Instant.now();
    }

    public ProfileId getId() { return id; }
    public Email getEmail() { return email; }
    public DisplayName getDisplayName() { return displayName; }
    public Instant getCreatedAt() { return createdAt; }

    /**
     * 更新顯示名稱。
     *
     * @param newDisplayName 新的顯示名稱
     */
    public void updateDisplayName(DisplayName newDisplayName) {
        if (newDisplayName == null || newDisplayName.value() == null) {
            throw new IllegalArgumentException("displayName cannot be null or empty for update");
        }
        this.displayName = newDisplayName;
    }
}
