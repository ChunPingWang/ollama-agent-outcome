package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 更新個人資料的應用程式層輸入 DTO。
 */
public record UpdateProfileRequest(
    String email,
    String displayName
) {
    public UpdateProfileRequest {
        Objects.requireNonNull(email, "Email cannot be null");
    }
}
