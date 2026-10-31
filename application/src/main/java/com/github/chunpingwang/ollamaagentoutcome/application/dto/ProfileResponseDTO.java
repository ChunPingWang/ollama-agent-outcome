package com.github.chunpingwang.ollamaagentoutcome.application.dto;

import java.util.UUID;

/**
 * 查詢個人資料時的回應 DTO。
 */
public record ProfileResponseDTO(UUID id, String email, String displayName, String bio) {
}
