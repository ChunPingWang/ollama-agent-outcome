package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增個人資料的請求 DTO。
 */
public class CreateProfileRequestDto {

    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @Size(max = 100, message = "display name length must not exceed 100")
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
