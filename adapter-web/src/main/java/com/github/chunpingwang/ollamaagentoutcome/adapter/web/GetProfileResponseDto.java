package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * 查詢個人資料的回應 DTO。
 */
public class GetProfileResponseDto {

    private UUID id;
    private String email;
    private String displayName;

    public GetProfileResponseDto(UUID id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
