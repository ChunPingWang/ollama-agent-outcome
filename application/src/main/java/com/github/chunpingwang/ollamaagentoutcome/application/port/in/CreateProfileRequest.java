package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 建立個人資料的請求 DTO。
 */
public record CreateProfileRequest(String email, String displayName) {
    public CreateProfileRequest {
        Objects.requireNonNull(email, "Email cannot be null");
        // displayName can be null or blank
    }
}
