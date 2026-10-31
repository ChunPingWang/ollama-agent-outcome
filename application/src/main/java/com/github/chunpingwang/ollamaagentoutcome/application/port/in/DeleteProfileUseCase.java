package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 刪除個人資料的使用者情境介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的情境。
     *
     * @param id 要刪除的個人資料唯一識別碼
     */
    void execute(UUID id);
}
