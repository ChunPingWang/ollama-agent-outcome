package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 刪除個人資料的使用案例介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的操作。
     *
     * @param id 個人資料識別碼
     */
    void execute(UUID id);
}
