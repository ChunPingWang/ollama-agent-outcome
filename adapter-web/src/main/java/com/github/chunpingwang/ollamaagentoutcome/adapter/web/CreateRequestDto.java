package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 建立個人資料的請求 DTO。
 */
public class CreateRequestDto {

    @NotBlank(message = "email 不能為空白")
    @Email(message = "email 格式無效")
    private String email;

    private String displayName;

    public CreateRequestDto() {}

    public CreateRequestDto(String email, String displayName) {
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
