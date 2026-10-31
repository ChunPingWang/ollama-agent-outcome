package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 找不到指定 ID 的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
