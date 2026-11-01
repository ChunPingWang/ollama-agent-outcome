package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或操作不存在的個人資料識別碼時所拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
