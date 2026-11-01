package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 載入個人資料的 Outbound Port 介面。
 */
public interface LoadProfilePort {

    /**
     * 根據識別碼查詢個人資料。
     *
     * @param id 個人資料識別碼
     * @return 找到的資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> findById(UUID id);
}
