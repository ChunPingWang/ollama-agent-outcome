package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的請求 DTO。
 */
public record UpdateProfileRequest(String email, String name, Integer age) {
}
