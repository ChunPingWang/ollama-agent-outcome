package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料實體。
 */
public record Profile(
        ProfileId id,
        Email email,
        Name name,
        Age age
) {
    /**
     * 返回更新後的 Profile 新實例（不可變設計）。
     */
    public Profile update(Name newName, Age newAge) {
        return new Profile(this.id, this.email, newName, newAge);
    }

    /**
     * 取得識別碼。
     */
    public ProfileId getId() {
        return id;
    }

    /**
     * 取得電子郵件。
     */
    public Email getEmail() {
        return email;
    }

    /**
     * 取得姓名。
     */
    public Name getName() {
        return name;
    }

    /**
     * 取得年齡。
     */
    public Age getAge() {
        return age;
    }
}
