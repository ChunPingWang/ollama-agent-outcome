package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當查詢不存在的識別碼時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("Profile not found with ID: " + id);
    }
}
