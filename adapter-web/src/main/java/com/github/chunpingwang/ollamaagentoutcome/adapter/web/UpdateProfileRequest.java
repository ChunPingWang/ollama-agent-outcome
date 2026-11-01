package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 更新個人資料的請求 DTO。
 */
public record UpdateProfileRequest(
    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    String email,

    String displayName
) {}
