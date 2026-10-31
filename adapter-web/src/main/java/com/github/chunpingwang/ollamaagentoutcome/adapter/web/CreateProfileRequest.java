package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 新增個人資料的請求 DTO。
 */
public record CreateProfileRequest(
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    String name,

    @NotNull(message = "email is required")
    @Email(message = "invalid email format")
    @Size(max = 254, message = "email must be at most 254 characters")
    String email
) {
}
