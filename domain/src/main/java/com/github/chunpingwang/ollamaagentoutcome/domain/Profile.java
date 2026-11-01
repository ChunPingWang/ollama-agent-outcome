package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Profile 聚合根，狀態透過方法更新並返回新實例（或修改內部 mutable state，此處採用不可變風格但為了 JPA 方便，實際領域邏輯上視為值物件組合）。
 * 注意：在純 Domain 層，我們通常設計為不可變。但在 JPA Entity 轉換時，Adapter 會處理映射。
 */
public class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    protected Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }

    /**
     * 建立新的 Profile。
     */
    public static Profile create(UUID id, String emailStr, String displayNameStr) {
        return new Profile(new ProfileId(id), new Email(emailStr), new DisplayName(displayNameStr));
    }

    public ProfileId getId() {
        return id;
    }

    public UUID getIdValue() {
        return id.value();
    }

    public Email getEmail() {
        return email;
    }

    public String getEmailString() {
        return email.value();
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public String getDisplayNameString() {
        return displayName.value();
    }

    /**
     * 更新個人資料。
     */
    public void update(String emailStr, String displayNameStr) {
        // 驗證邏輯在 Email/DisplayName constructor 中處理，若無效會拋出 InvalidEmailException
        this.email = new Email(emailStr);
        this.displayName = new DisplayName(displayNameStr);
    }
}
