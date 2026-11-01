package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料請求的 DTO。
 */
public record UpdateProfileRequest(
        @NotBlank(message = "電子郵件不能為空白")
        @Email(message = "電子郵件格式無效")
        String email,

        @NotBlank(message = "顯示名稱不能為空白")
        @Size(max = 255, message = "長度不得超過 255")
        String displayName) {}
