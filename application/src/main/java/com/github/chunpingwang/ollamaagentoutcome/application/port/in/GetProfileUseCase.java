package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢個人資料的 Inbound Port 介面。
 */
public interface GetProfileUseCase {

    /**
     * 執行查詢個人資料的業務邏輯。
     *
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> execute(UUID id);
}
