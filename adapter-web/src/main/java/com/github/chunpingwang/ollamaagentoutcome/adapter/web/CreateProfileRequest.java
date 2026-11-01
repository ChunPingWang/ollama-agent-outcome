package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 新增個人資料的請求 DTO。
 */
public class CreateProfileRequest {

    @NotBlank(message = "must not be blank")
    private String email;

    private String displayName;

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
