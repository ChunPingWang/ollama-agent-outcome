package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Profile Aggregate Root。負責維護個人資料的業務規則與不變條件。
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    // 用於 JPA 或測試用的無參建構子（實際邏輯應透過靜態工廠或應用層建立）
    protected Profile() {
        this.id = new ProfileId(UUID.randomUUID());
        this.email = null;
        this.displayName = null;
    }

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 更新顯示名稱。若為空白則設為空值。
     */
    public void updateDisplayName(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.displayName = new DisplayName(newName);
        } else {
            this.displayName = new DisplayName(null);
        }
    }

    /**
     * 更新電子郵件地址。會觸發格式驗證。
     */
    public void updateEmail(String newEmailStr) {
        this.email = new Email(newEmailStr);
    }

    public ProfileId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return id.equals(profile.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
