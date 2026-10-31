package com.github.chunpingwang.ollamaagentoutcome.domain;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;

/**
 * 個人資料 Entity，代表一個完整的個人資料聚合根。
 */
public record Profile(ProfileId id, String name, Email email) {

    public Profile {
        if (id == null) {
            throw new IllegalArgumentException("ProfileId cannot be null");
        }
        if (name == null) {
            throw new InvalidNameException("Name cannot be null");
        }
        if (name.isBlank()) {
            throw new InvalidNameException("Name cannot be blank");
        }
        if (email == null) {
            throw new InvalidEmailException("Email cannot be null");
        }
    }

    /**
     * 更新姓名。
     *
     * @param newName 新的姓名
     * @return 更新後的 Profile 實例
     */
    public Profile updateName(String newName) {
        if (newName == null) {
            throw new InvalidNameException("Name cannot be null");
        }
        if (newName.isBlank()) {
            throw new InvalidNameException("Name cannot be blank");
        }
        // 建立新的 record 實例，確保不可變性
        return new Profile(id, newName, email);
    }

    /**
     * 更新電子郵件。
     *
     * @param newEmail 新的電子郵件
     * @return 更新後的 Profile 實例
     */
    public Profile updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new InvalidEmailException("Email cannot be null");
        }
        // 建立新的 record 實例，確保不可變性
        return new Profile(id, name, newEmail);
    }
}
