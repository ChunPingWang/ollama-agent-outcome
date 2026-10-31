package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 *
 * @param name  姓名
 * @param email 電子郵件
 * @param note  備註
 */
record UpdateProfileRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @Size(max = 500, message = "Note must be at most 500 characters")
        String note
) {
}
