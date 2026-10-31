package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料 Entity，包含核心業務邏輯與不變條件檢查。
 */
public class Profile {

    private final ProfileId id;
    private Name name;
    private Email email;

    public Profile(ProfileId id, Name name, Email email) {
        this.id = id;
        updateName(name);
        updateEmail(email);
    }

    public ProfileId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Email email() {
        return email;
    }

    /**
     * 更新姓名，會驗證新名稱的有效性。
     */
    public void updateName(Name newName) {
        if (newName == null || newName.text() == null || newName.text().isBlank()) {
            throw new InvalidProfileException("name is required and cannot be blank");
        }
        this.name = newName;
    }

    /**
     * 更新電子郵件，會驗證新信箱格式的有效性。
     */
    public void updateEmail(Email newEmail) {
        if (newEmail == null || newEmail.address() == null || newEmail.address().isBlank()) {
            throw new InvalidProfileException("email format is invalid");
        }
        this.email = newEmail;
    }
}
