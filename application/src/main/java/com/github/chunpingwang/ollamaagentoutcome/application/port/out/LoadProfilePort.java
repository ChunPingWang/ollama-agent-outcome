package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * 載入個人資料的外向 Port (Outbound Port)。
 */
public interface LoadProfilePort {

    /**
     * 根據識別碼查找個人資料。
     *
     * @param id 個人資料識別碼
     * @return 找到的資料，否則為空 Optional
     */
    Optional<Profile> findById(ProfileId id);
}
