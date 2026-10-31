package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * 個人資料回應 DTO。
 */
public record ProfileResponse(UUID id, String name, String email, String bio) {
}
