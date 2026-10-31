package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile 聚合根：包含基本個人資料與業務規則驗證。
 */
public class Profile {

    private ProfileId id;
    private Email email;
    private DisplayName displayName;

    protected Profile() {
        // For JPA mapping only
    }

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        // 允許顯示名稱為 null，符合規格中「未設定」的語意
        this.displayName = displayName;
    }

    public void updateDisplayName(DisplayName newDisplayName) {
        this.displayName = Objects.requireNonNull(newDisplayName, "New display name cannot be null");
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
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
