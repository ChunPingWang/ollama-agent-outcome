package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import java.util.UUID;

/**
 * 找不到個人資料例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with ID: " + id);
    }
}
