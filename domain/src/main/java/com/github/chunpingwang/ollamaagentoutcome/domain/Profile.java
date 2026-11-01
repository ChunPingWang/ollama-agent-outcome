package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料 Domain Entity，包含核心業務規則。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * 私有建構子，強制使用靜態工廠方法建立實體。
     */
    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.displayName = Objects.requireNonNull(displayName);
    }

    /**
     * 靜態工廠方法：建立新的個人資料。
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
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
     * 更新個人資料的電子郵件與顯示名稱。
     * 若提供新 Email，將驗證格式；若為相同 Email 則不視為重複錯誤。
     */
    public void update(Email newEmail, DisplayName newDisplayName) {
        if (newEmail != null && !Objects.equals(this.email.value(), newEmail.value())) {
            // 如果提供了不同的 email，VO 建構子會自動處理格式驗證
            this.email = newEmail; 
        } else if (newEmail == null) {
             // 若不改變 email，保持原樣
        }
        
        if (newDisplayName != null) {
            this.displayName = newDisplayName;
        }
    }
}
