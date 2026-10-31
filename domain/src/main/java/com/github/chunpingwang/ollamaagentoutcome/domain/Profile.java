package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料實體。
 */
public record Profile(
        ProfileId id,
        Email email,
        Name name,
        Bio bio
) {
    /**
     * 更新姓名與簡介，回傳新的 Profile 實例。
     */
    public Profile update(Name newName, Bio newBio) {
        return new Profile(this.id, this.email, newName, newBio);
    }
}
