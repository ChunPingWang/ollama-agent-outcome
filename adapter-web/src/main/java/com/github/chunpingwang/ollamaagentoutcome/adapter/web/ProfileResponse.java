package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

/**
 * 查詢個人資料的回傳 DTO。
 */
public record ProfileResponse(
    String id,
    String email,
    String displayName
) {}
