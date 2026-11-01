package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * 載入個人資料的 Outbound Port。
 */
public interface LoadProfilePort {
    /**
     * 根據識別碼查詢個人資料。
     * @param id 個人資料識別碼
     * @return Optional 包含找到的資料，若不存在則為空
     */
    Optional<Profile> findById(ProfileId id);
}
