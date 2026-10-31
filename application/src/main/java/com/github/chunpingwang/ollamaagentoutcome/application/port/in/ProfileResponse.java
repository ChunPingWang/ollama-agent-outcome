package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 查詢個人資料的回應 DTO。
 */
public record ProfileResponse(String id, String email, String displayName, String bio) {
}
