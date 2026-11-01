package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料聚合根。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * 建立新的個人資料，驗證輸入參數的有效性。
     */
    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id cannot be null");
        // 先檢查 email 是否為 null，以區分「未提供」與「格式錯誤」
        if (email == null) {
            throw new NullPointerException("email cannot be null");
        }
        
        this.id = id;
        this.email = email;
        updateDisplayName(displayName);
    }

    public ProfileId getId() {
        return id;
    }

    public String getEmailValue() {
        return email.value();
    }

    public void setEmail(Email newEmail) {
        if (newEmail == null || newEmail.value().isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        this.email = newEmail;
    }

    public String getDisplayNameValue() {
        return displayName.isPresent() ? displayName.value() : "";
    }

    /**
     * 更新顯示名稱，若為空則設為未設定。
     */
    public void updateDisplayName(DisplayName newName) {
        this.displayName = newName != null ? newName : new DisplayName(null);
    }

    public boolean hasSameEmail(String otherEmail) {
        return this.email.value().equals(otherEmail);
    }
}
