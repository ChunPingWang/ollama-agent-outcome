package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 找不到個人資料例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(ProfileId id) {
        super("Profile not found with id: " + id);
    }
}
