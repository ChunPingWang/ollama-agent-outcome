package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 個人資料查詢結果 DTO。
 */
public record ProfileResult(UUID id, String name, String email, String bio) {
}
