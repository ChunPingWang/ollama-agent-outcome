package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料領域模型 (Entity)。
 * 此為不可變物件，更新時回傳新實例。
 */
public record Profile(
        ProfileId id,
        String name,
        Email email
) {
    public Profile {
        if (name == null || name.isBlank()) {
            throw new InvalidProfileException("name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new InvalidProfileException("name cannot be longer than 100 characters");
        }
        if (email == null) {
            throw new InvalidProfileException("email cannot be null");
        }
        if (email.value().isBlank()) {
            throw new InvalidProfileException("email cannot be empty");
        }
    }

    /**
     * 更新個人資料，回傳新的 Profile 實例。
     *
     * @param newName 新的姓名
     * @param newEmail 新的電子郵件
     * @return 更新後的 Profile 實例
     */
    public Profile update(String newName, Email newEmail) {
        return new Profile(id, newName, newEmail);
    }
}
