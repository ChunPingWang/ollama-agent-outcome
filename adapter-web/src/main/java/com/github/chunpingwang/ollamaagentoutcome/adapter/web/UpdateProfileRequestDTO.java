package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequestDTO(
        @NotBlank(message = "email must not be blank")
        @Email(message = "invalid email format") String email,

        String displayName) {
}
