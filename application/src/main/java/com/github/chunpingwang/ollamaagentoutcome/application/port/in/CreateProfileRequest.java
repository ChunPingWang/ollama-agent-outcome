package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 用於接收新增個人資料請求的 DTO。
 */
public final class CreateProfileRequest {

    private final String email;
    private final String displayName;

    public CreateProfileRequest(String email, String displayName) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
