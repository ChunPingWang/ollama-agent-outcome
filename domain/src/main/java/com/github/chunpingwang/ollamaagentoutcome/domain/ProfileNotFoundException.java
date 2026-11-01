package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或操作不存在的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String id) {
        super("Profile not found with ID: " + id);
    }
}
