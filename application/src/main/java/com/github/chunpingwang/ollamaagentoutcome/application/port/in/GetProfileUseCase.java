package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的應用層入口介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> getById(ProfileId id);
}
