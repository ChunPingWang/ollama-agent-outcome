package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile Aggregate Root。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        
        this.id = id;
        this.email = email;
        updateDisplayName(displayName);
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

    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new InvalidEmailException("New email cannot be null");
        }
        this.email = newEmail;
    }

    public void updateDisplayName(DisplayName newDisplayName) {
        // 允許 null，內部會處理為空字串或保留原值（若呼叫端傳 null）
        if (newDisplayName != null) {
            this.displayName = newDisplayName;
        } else {
             // 如果明確要清除，可設為 empty display name，這裡假設不改變除非提供新值
             // 但為了符合 DTO 更新邏輯，通常外部會傳入新的 DisplayName object
             // 此處簡化：若傳入非 null 則覆蓋
        }
    }
    
    /**
     * 檢查是否為重複電子郵件的領域層輔助方法（實際唯一性由 DB 約束保證）。
     */
    public boolean isSameEmailAs(Profile other) {
        if (other == null || other.getEmail() == null) {
            return false;
        }
        return Objects.equals(this.email.value(), other.email.value());
    }
}
