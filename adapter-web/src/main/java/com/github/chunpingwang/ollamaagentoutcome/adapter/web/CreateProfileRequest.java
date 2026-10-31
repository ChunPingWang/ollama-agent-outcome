package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 建立個人資料的請求 DTO。
 */
record CreateProfileRequest(
        @NotBlank @Email String email,
        @NotBlank String name,
        @Min(1) Integer age
) {
}
