package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢的個人資料不存在時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
