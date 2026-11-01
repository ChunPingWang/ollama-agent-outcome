package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * 查詢個人資料的回應 DTO。
 */
public record GetProfileResponseDto(UUID id, String email, String displayName) {}
