package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 找不到個人資料時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("Profile not found with id: " + id);
    }
}
