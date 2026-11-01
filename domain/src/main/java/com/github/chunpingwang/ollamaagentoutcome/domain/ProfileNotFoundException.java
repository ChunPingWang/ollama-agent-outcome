package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當查詢或刪除的 ID 不存在時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    private final UUID id;

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
