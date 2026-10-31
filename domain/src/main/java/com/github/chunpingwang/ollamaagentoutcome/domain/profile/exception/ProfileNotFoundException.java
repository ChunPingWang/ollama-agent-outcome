package com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception;

import java.util.UUID;

/**
 * 找不到指定 ID 的個人資料時拋出的領域例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
}
