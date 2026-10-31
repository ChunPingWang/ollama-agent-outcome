package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 */
public record UpdateProfileRequest(
        @NotBlank(message = "姓名不可為空")
        @Size(max = 100, message = "姓名長度不可超過 100")
        String name,

        @NotBlank(message = "電子郵件不可為空")
        @Email(message = "電子郵件格式不正確")
        String email
) {
}
