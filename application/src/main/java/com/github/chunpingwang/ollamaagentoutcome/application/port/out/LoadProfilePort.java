package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 載入個人資料的外向端口介面。
 */
public interface LoadProfilePort {

    /**
     * 根據識別碼載入個人資料。
     *
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> findById(ProfileId id);
}
