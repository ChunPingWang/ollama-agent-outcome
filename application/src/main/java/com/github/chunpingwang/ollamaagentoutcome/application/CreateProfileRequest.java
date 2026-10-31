package com.github.chunpingwang.ollamaagentoutcome.application;

/**
 * 建立個人資料的輸入 DTO。
 */
public record CreateProfileRequest(
        String name,
        String email,
        String note
) {
}
