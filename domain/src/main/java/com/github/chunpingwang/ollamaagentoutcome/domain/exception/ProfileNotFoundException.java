package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 當查詢指定 ID 的 Profile 不存在時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(ProfileId id) {
        super("Profile not found with id: " + id);
    }
}
