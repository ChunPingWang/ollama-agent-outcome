package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 更新個人資料的 Inbound Port 介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param id 個人資料識別碼
     * @param email 新的電子郵件地址
     * @param displayName 新的顯示名稱
     */
    void execute(UUID id, String email, String displayName);
}
