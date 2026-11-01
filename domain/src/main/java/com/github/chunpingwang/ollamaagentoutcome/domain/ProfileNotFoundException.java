package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當查詢或操作不存在的 ID 時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
    
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
