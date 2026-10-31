package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 用於查詢個人資料的查詢物件。
 */
public record GetProfileQuery(UUID id) {
}
