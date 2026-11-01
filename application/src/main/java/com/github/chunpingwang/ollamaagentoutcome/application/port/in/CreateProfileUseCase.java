package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    /**
     * 執行建立個人資料的業務邏輯。
     * @param email 電子郵件地址
     * @param displayName 顯示名稱
     */
    void execute(UUID id, String email, String displayName);
}
