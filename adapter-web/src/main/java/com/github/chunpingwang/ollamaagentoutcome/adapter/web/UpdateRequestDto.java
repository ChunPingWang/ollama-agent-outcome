package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

/**
 * 更新個人資料的請求 DTO。
 */
public class UpdateRequestDto {

    // Email is optional in update request as the business logic does not require it for identification
    private String email;

    private String displayName;

    public UpdateRequestDto() {}

    public UpdateRequestDto(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
