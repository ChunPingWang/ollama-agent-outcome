package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當指定的個人資料 ID 不存在時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    
    public ProfileNotFoundException(ProfileId id) {
        super("profile not found with id: " + id.value());
    }
}
