package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 */
public class UpdateProfileRequestDto {

    @Size(max = 100, message = "display name length must not exceed 100")
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
