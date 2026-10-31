package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

/**
 * 找不到指定識別碼的個人資料時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(ProfileId id) {
        super("Profile not found with ID: " + id.value());
    }
}
