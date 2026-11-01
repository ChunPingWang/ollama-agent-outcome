package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 建立個人資料的應用程式層輸入 DTO。
 */
public record CreateProfileRequest(
    String email,
    String displayName
) {
    public CreateProfileRequest {
        Objects.requireNonNull(email, "Email cannot be null");
    }
}
