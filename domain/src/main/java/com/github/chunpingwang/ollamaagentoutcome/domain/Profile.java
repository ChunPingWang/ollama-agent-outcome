package com.github.chunpingwang.ollamaagentoutcome.domain;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;

/**
 * 個人資料 Entity（聚合根）。
 */
public class Profile {

    private final ProfileId id;
    private String name;
    private Email email;

    /**
     * 建立 Profile。
     *
     * @param id 識別碼，不可為 null
     * @param name 姓名，不可為 null 或空白
     * @param email 電子郵件，不可為 null
     * @throws IllegalArgumentException 如果 id 為 null
     * @throws InvalidNameException 如果 name 為 null 或空白
     * @throws IllegalArgumentException 如果 email 為 null
     */
    public Profile(ProfileId id, String name, Email email) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null) {
            throw new InvalidNameException("Name cannot be null");
        }
        if (name.isBlank()) {
            throw new InvalidNameException("Name cannot be blank");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        this.id = id;
        this.name = name;
        this.email = email;
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
     * 取得姓名。
     *
     * @return 姓名
     */
    public String getName() {
        return name;
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
     * 更新姓名。
     *
     * @param newName 新姓名，不可為 null 或空白
     * @throws InvalidNameException 如果 newName 為 null 或空白
     */
    public void updateName(String newName) {
        if (newName == null) {
            throw new InvalidNameException("Name cannot be null");
        }
        if (newName.isBlank()) {
            throw new InvalidNameException("Name cannot be blank");
        }
        this.name = newName;
    }

    /**
     * 更新電子郵件。
     *
     * @param newEmail 新電子郵件
     * @throws InvalidEmailException 如果 newEmail 格式無效
     */
    public void updateEmail(Email newEmail) {
        // Email constructor 已處理 null 與格式驗證
        this.email = newEmail;
    }
}
