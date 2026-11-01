package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或操作不存在的 ID 時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    private final String id;

    public ProfileNotFoundException(String id) {
        super("profile not found with id: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
