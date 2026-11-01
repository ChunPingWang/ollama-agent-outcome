package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 刪除個人資料的 Inbound Port 介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據 ID 刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void execute(UUID id);
}
