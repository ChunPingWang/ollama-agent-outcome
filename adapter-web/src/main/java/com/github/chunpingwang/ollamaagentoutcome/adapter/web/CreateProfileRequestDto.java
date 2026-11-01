package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增個人資料的請求 DTO。
 */
public record CreateProfileRequestDto(
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    String email,

    @Size(max = 100, message = "Display name too long (max 100)")
    String displayName
) {}
