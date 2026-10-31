package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 建立個人資料的請求 DTO。
 */
public record CreateProfileRequest(
        @NotBlank(message = "name is required")
        @Size(max = 50, message = "name must be at most 50 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @Size(max = 255, message = "bio must be at most 255 characters")
        String bio) {
}
