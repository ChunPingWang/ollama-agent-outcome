package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 查詢個人資料的回傳 DTO。
 */
public record GetProfileResponse(String id, String email, String displayName) {
    public GetProfileResponse {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
    }
}
