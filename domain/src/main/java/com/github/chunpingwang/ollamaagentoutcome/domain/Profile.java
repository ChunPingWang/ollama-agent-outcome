package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料 Entity。
 * 代表一個使用者的基本資訊，包含識別碼、電子郵件與姓名。
 * 此物件為不可變物件（Immutable）。
 */
public final class Profile {

    private final ProfileId id;
    private final Email email;
    private final String name;

    /**
     * 建立新的個人資料。
     *
     * @param id    識別碼，不得為 null
     * @param email 電子郵件，不得為 null
     * @param name  姓名，不得為 null 或空白
     * @throws IllegalArgumentException 若必要欄位為 null 或 name 為空白
     */
    public Profile(ProfileId id, Email email, String name) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.id = id;
        this.email = email;
        this.name = name;
    }

    /**
     * 取得識別碼。
     *
     * @return 識別碼
     */
    public ProfileId getId() {
        return id;
    }

    /**
     * 取得電子郵件。
     *
     * @return 電子郵件
     */
    public Email getEmail() {
        return email;
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
     * 更新個人資料的姓名。
     * 由於 Profile 是不可變物件，此方法會回傳一個新的 Profile 實例。
     *
     * @param newName 新的姓名，不得為 null 或空白
     * @return 更新姓名後的新 Profile 實例
     * @throws IllegalArgumentException 若 newName 為 null 或空白
     */
    public Profile updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        return new Profile(this.id, this.email, newName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id.equals(profile.id) && email.equals(profile.email) && name.equals(profile.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Profile{id=" + id + ", email=" + email + ", name='" + name + "'}";
    }
}
