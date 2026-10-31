package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;
import java.util.UUID;

/**
 * 查詢單一個人資料的使用者情境介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param id 個人資料的唯一識別碼
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile> execute(UUID id);
}
