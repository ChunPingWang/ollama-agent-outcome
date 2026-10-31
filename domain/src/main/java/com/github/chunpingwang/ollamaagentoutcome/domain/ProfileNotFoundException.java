package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當指定的識別碼找不到對應的個人資料時拋出。
 */
public class ProfileNotFoundException extends InvalidProfileException {

    public ProfileNotFoundException(Object id) {
        super("profile not found with id=" + id);
    }
}
