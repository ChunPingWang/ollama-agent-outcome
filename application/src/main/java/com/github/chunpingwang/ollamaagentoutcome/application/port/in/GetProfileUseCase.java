package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * 查詢個人資料的 Inbound Port。
 */
public interface GetProfileUseCase {
    /**
     * 執行查詢個人資料的業務邏輯。
     *
     * @param id 識別碼
     * @return 找到的個人資料，若不存在則回傳空 Optional
     */
    Optional<Profile> execute(ProfileId id);
}
