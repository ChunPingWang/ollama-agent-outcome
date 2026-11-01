package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體。包含基本資訊與業務行為。
 */
public final class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    private Profile(ProfileId id, Email email, DisplayName displayName) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        this.id = id;
        this.email = email;
        this.displayName = (displayName != null && displayName.name().isBlank()) ? new DisplayName("") : displayName;
    }

    /**
     * 靜態工廠方法，建立新的個人資料實體。
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
     * 更新電子郵件與顯示名稱。
     */
    public void update(Email email, DisplayName displayName) {
        Objects.requireNonNull(email, "email must not be null for update");
        this.email = email;
        if (displayName != null && !displayName.name().isBlank()) {
            this.displayName = displayName;
        } else {
            // If blank or null provided during update, keep existing or set to empty? 
            // Spec says display name allows null/blank. Let's allow explicit setting to blank/null logic handled by VO constructor usually, 
            // but here we just accept the passed value if it's valid. 
            // To support clearing display name, we might need a specific method or allow passing an empty Display Name.
            // For now, simple assignment. If caller wants to clear, they pass new DisplayName("").
            this.displayName = displayName;
        }
    }
}
