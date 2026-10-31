package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import jakarta.validation.constraints.Size;

/**
 * 更新個人資料的請求 DTO。
 */
record UpdateProfileRequestDto(
        @Size(max = 100, message = "Display name too long")
        String displayName) {
}
