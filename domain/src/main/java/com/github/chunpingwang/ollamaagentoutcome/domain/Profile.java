package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料聚合根，包含基本資訊與更新邏輯。
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private Name name;

    /**
     * 建立新的個人資料。
     *
     * @param id   識別碼
     * @param name 姓名
     * @param email 電子郵件
     */
    public Profile(ProfileId id, Name name, Email email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * 取得識別碼（不可變）。
     */
    public ProfileId id() {
        return id;
    }

    /**
     * 取得電子郵件。
     */
    public Email email() {
        return email;
    }

    /**
     * 取得姓名。
     */
    public Name name() {
        return name;
    }

    /**
     * 更新姓名。
     *
     * @param newName 新姓名值物件
     */
    public void updateName(Name newName) {
        this.name = newName;
    }

    /**
     * 更新電子郵件，會重新驗證格式。
     *
     * @param newEmail 新郵件位址值物件
     * @throws InvalidEmailException 當新格式無效時
     */
    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }
}
