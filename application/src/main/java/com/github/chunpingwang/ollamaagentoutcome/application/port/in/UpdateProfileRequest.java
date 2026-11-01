package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 更新個人資料的請求 DTO。
 */
public record UpdateProfileRequest(String email, String displayName) {
    public UpdateProfileRequest {
        Objects.requireNonNull(email, "email cannot be null");
        // displayName can be null or blank, handled by domain layer
    }
}
