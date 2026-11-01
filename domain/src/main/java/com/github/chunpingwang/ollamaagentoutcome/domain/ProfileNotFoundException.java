package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當根據 ID 找不到資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
