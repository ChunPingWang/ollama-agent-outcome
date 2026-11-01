package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當找不到指定 ID 的個人資料時拋出的例外。
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
