package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

/**
 * 查詢個人資料的回應 DTO。
 */
public record GetProfileResponse(String id, String email, String displayName) {}
