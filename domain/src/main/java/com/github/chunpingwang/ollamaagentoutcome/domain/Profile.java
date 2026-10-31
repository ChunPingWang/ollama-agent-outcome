package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料 Entity。
 * 採用不可變設計，更新時建立新實例。
 */
public record Profile(
        ProfileId id,
        Email email,
        Name name,
        Age age
) {

    /**
     * 建立一個更新後的 Profile 新實例。
     * 原實例保持不變。
     * 此方法僅更新姓名與年齡，電子郵件保持不變。
     *
     * @param newName 新的姓名
     * @param newAge 新的年齡
     * @return 更新後的 Profile 實例
     */
    public Profile update(Name newName, Age newAge) {
        return new Profile(this.id, this.email, newName, newAge);
    }

    /**
     * 建立一個完全更新的 Profile 新實例。
     * 原實例保持不變。
     *
     * @param newEmail 新的電子郵件
     * @param newName 新的姓名
     * @param newAge 新的年齡
     * @return 更新後的 Profile 實例
     */
    public Profile update(Email newEmail, Name newName, Age newAge) {
        return new Profile(this.id, newEmail, newName, newAge);
    }
}
