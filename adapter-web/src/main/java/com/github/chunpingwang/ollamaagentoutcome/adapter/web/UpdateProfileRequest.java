package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequest(
    @NotBlank(message = "姓名不可為空")
    String name,
    @NotBlank(message = "電子郵件不可為空")
    @Email(message = "電子郵件格式無效")
    String email,
    String bio
) {
    /**
     * 轉換為 Application 層的 Use Case Request。
     */
    UpdateProfileUseCase.UpdateProfileRequest toUseCaseRequest() {
        return new UpdateProfileUseCase.UpdateProfileRequest(email, name, bio);
    }
}
