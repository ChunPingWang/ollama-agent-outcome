package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 建立個人資料的請求 DTO。
 */
public record CreateProfileRequest(String email, String displayName) {
    public CreateProfileRequest {
        Objects.requireNonNull(email, "email must not be null");
    }
}
