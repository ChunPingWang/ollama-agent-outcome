package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 依 ID 查詢個人資料的 Outbound Port。
 */
public interface FindProfileByIdPort {

    /**
     * 根據識別碼查找個人資料。
     *
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> findById(ProfileId id);
}
