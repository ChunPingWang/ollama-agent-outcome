package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體，包含核心屬性與業務行為。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;

    /**
     * 建立新的個人資料。
     *
     * @param id         識別碼
     * @param email      電子郵件
     * @param displayName 顯示名稱
     */
    private Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = displayName;
    }

    /**
     * 靜態工廠方法：建立新的個人資料。
     *
     * @param id          識別碼
     * @param email       電子郵件值物件
     * @param displayName 顯示名稱值物件（可為 null）
     * @return 新建的 Profile 實例
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
    }

    /**
     * 取得識別碼。
     *
     * @return ProfileId
     */
    public ProfileId id() {
        return id;
    }

    /**
     * 取得電子郵件。
     *
     * @return Email
     */
    public Email email() {
        return email;
    }

    /**
     * 取得顯示名稱。
     *
     * @return DisplayName，可能為 null
     */
    public DisplayName displayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱。
     *
     * @param newDisplayName 新的顯示名稱值物件
     */
    public void updateDisplayName(DisplayName newDisplayName) {
        this.displayName = Objects.requireNonNull(newDisplayName, "displayName must not be null");
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
