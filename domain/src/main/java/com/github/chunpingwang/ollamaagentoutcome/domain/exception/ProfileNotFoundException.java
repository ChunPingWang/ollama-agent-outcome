package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import java.util.UUID;

/**
 * 當查詢或刪除找不到對應 ID 的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
}
