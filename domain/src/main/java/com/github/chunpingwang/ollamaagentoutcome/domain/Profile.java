package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 個人資料領域實體。
 * 採用不可變設計，更新時建立新實例。
 */
public final class Profile {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final ProfileId id;
    private final String email;
    private final String displayName;
    private final String bio;

    public Profile(ProfileId id, String email, String displayName, String bio) {
        this.id = Objects.requireNonNull(id, "ProfileId cannot be null");
        validateEmail(email);
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    private static void validateEmail(String email) {
        if (email == null) {
            throw new InvalidEmailException("Email cannot be null");
        }
        if (email.isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    public ProfileId id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String displayName() {
        return displayName;
    }

    public String bio() {
        return bio;
    }

    /**
     * 建立一個更新後的 Profile 實例。
     * 原始實例保持不變。
     *
     * @param newDisplayName 新的顯示名稱
     * @param newBio         新的簡介
     * @return 新的 Profile 實例
     */
    public Profile update(String newDisplayName, String newBio) {
        // 驗證新的 email 格式（雖然此方法不接收 email，但為確保一致性，若未來擴充需檢查）
        // 此處僅更新 displayName 和 bio，email 保持不變，故不需再次驗證 email 格式
        // 但若需驗證 email 變動，應在呼叫端或新的 updateEmail 方法中處理
        return new Profile(id, email, newDisplayName, newBio);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id) &&
                Objects.equals(email, profile.email) &&
                Objects.equals(displayName, profile.displayName) &&
                Objects.equals(bio, profile.bio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, displayName, bio);
    }
}
