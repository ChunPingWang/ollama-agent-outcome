package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當找不到指定識別碼的個人資料時拋出的領域例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
}
