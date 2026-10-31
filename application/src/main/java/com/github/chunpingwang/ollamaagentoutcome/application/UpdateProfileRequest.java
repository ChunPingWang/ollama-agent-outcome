package com.github.chunpingwang.ollamaagentoutcome.application;

/**
 * 更新個人資料的輸入 DTO。
 */
public record UpdateProfileRequest(
        String name,
        String email,
        String note
) {
}
