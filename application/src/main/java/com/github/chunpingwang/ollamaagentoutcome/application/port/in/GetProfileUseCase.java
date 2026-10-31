package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的 Inbound Port。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼查詢個人資料。
     *
     * @param id 個人資料識別碼
     * @return 個人資料
     */
    Profile getProfile(ProfileId id);
}
