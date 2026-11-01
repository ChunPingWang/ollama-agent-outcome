package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

/**
 * 個人資料回應 DTO。
 */
public class ProfileResponseDto {

    private final String id;
    private final String email;
    private final String displayName;

    public ProfileResponseDto(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
