package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料不存在例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
