package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * 查詢個人資料的 Inbound Port。
 */
public interface GetProfileUseCase {
    /**
     * 根據 ID 查詢個人資料。
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳空 Optional
     */
    Optional<Profile> findById(ProfileId id);
}
