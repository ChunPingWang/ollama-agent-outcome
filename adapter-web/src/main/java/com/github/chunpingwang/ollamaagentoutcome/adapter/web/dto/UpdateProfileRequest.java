package com.github.chunpingwang.ollamaagentoutcome.adapter.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 */
public record UpdateProfileRequest(
        @NotNull(message = "ID cannot be null for update")
        String id,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Display name is required")
        @Size(max = 50, message = "Display name must not exceed 50 characters")
        String displayName,

        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio) {
}
