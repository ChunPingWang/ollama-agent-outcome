package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 找不到個人資料例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
