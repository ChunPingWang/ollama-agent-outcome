package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料聚合根。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * 建立新的 Profile 實體。
     *
     * @param id       識別碼
     * @param email    電子郵件
     * @param displayName 顯示名稱
     * @throws NullPointerException 若 id 或 email 為 null
     */
    public Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null for creation");
        
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * 取得識別碼。
     *
     * @return ProfileId
     */
    public ProfileId getId() {
        return id;
    }

    /**
     * 取得電子郵件。
     *
     * @return Email
     */
    public Email getEmail() {
        return email;
    }

    /**
     * 更新電子郵件（需確保新值有效）。
     *
     * @param email 新的電子郵件
     */
    public void setEmail(Email email) {
        if (email != null && !this.email.equals(email)) {
            // 假設呼叫端已驗證格式，此處僅做基本檢查以防萬一
             if (email.getValue().isBlank()) {
                 throw new InvalidEmailException("email cannot be blank");
             }
        }
        this.email = email;
    }

    /**
     * 取得顯示名稱。
     *
     * @return DisplayName
     */
    public DisplayName getDisplayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱。
     *
     * @param displayName 新的顯示名稱
     */
    public void updateDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    /**
     * 靜態工廠方法：建立新的 Profile 實體並產生 UUID。
     *
     * @param uuid           識別碼 UUID
     * @param emailValue     電子郵件字串
     * @param displayNameValue 顯示名稱字串
     * @return 新建的 Profile 實例
     */
    public static Profile create(UUID uuid, String emailValue, String displayNameValue) {
        return new Profile(new ProfileId(uuid), new Email(emailValue), new DisplayName(displayNameValue));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
