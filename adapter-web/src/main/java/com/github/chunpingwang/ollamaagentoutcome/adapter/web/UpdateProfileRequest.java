package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Age cannot be null")
        @Min(value = 1, message = "Age must be a positive integer")
        Integer age
) {
}
