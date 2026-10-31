package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Profile Aggregate Root，負責領域邏輯與狀態管理。
 * 注意：此類別為純 Domain Object，不依賴任何框架 (Spring/JPA)。
 */
public class Profile {

    private final UUID id;
    private final Email email;
    private DisplayName displayName;
    private Bio bio;
    private final Instant createdAt;
    private Instant updatedAt;
    
    /**
     * JPA Persistable 支援欄位，用於追蹤實體是否已持久化。
     */
    private transient boolean isNew = true;

    protected Profile() {
        // For JPA instantiation only
        this.id = null;
        this.email = null;
        this.displayName = null;
        this.bio = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.isNew = false;
    }

    public Profile(UUID id, Email email, DisplayName displayName, Bio bio) {
        if (id == null || id.toString().isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.isNew = true;
    }

    /**
     * 更新顯示名稱與簡介。Email 不可更改（需透過 Use Case 層處理唯一性檢查）。
     */
    public void update(DisplayName newDisplayName, Bio newBio) {
        this.displayName = newDisplayName;
        this.bio = newBio;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public Bio getBio() {
        return bio;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Profile{id=" + id + ", email=" + (email != null ? email.address() : null) + "}";
    }

    // --- Persistable Support Methods ---

    /**
     * 判斷此 Profile 是否為新建立尚未持久化的實體。
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * 標記此 Profile 已非新狀態（通常由 JPA 回載或轉換器呼叫）。
     */
    void markNotNew() {
        this.isNew = false;
    }
}
