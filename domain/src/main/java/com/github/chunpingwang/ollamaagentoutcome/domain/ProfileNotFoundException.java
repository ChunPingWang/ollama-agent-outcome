package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或更新不存在的個人資料時拋出的例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String profileId) {
        super("Profile not found: " + profileId);
    }
}
