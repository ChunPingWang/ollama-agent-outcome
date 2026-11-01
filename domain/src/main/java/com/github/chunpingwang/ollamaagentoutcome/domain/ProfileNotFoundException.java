package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當找不到指定的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("Profile not found with ID: " + id);
    }
}
