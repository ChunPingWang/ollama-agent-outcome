package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {
    /**
     * 執行刪除個人資料的業務邏輯。
     * @param id 個人資料識別碼
     */
    void execute(UUID id);
}
