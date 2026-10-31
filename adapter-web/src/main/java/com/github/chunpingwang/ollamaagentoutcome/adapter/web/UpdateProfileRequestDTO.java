package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequestDTO(
    @NotNull(message = "識別碼不能為空")
    String id,

    @NotBlank(message = "姓名不能為空")
    @Size(max = 50, message = "姓名長度不得超過 50")
    String name,

    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    String email
) {}
