package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 建立個人資料的請求 DTO。
 */
public record CreateProfileRequest(String email, String name, Integer age) {
}
