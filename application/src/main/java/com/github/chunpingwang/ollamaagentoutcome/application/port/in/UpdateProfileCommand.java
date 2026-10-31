package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 用於更新個人資料的指令記錄。
 *
 * @param id          要更新的個人資料唯一識別碼
 * @param email       新的電子郵件地址
 * @param displayName 新的顯示名稱
 * @param bio         新的個人簡介（可選）
 */
public record UpdateProfileCommand(UUID id, String email, String displayName, String bio) {
}
