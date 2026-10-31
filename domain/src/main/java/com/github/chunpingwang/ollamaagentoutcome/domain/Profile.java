package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料實體，封裝基本狀態與業務規則。
 */
public class Profile {

    private final UUID id;
    private Name name;
    private Email email;

    /**
     * 建立新的個人資料。
     *
     * @param id       唯一識別碼
     * @param nameValue 姓名
     * @param emailValue 電子郵件
     */
    public Profile(UUID id, String nameValue, String emailValue) {
        this.id = new ProfileId(id).value();
        this.name = new Name(nameValue);
        this.email = new Email(emailValue);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name.value();
    }

    /**
     * 更新姓名。
     *
     * @param newName 新姓名
     */
    public void setName(String newName) {
        this.name = new Name(newName);
    }

    public String getEmail() {
        return email.value();
    }

    /**
     * 更新電子郵件。
     *
     * @param newEmail 新電子郵件
     */
    public void setEmail(String newEmail) {
        this.email = new Email(newEmail);
    }
}
