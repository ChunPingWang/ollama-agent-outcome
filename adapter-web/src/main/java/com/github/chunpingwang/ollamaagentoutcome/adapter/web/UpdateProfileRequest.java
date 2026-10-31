package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequest(
        @NotBlank @Email String email,
        @NotBlank String name,
        @Min(1) Integer age
) {
}
