package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * 查詢個人資料的回傳 DTO。
 */
public record ProfileResponse(UUID id, String name, String email) {
}
