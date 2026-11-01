package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的用例介面 (Inbound Port)。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼取得個人資料結果。
     *
     * @param id 個人資料識別碼
     * @return 包含資料內容的結果物件
     */
    ProfileResult execute(ProfileId id);

    /**
     * 查詢結果 Value Object，封裝從 Domain Entity 提取的數據。
     */
    record ProfileResult(String email, String displayName) {}
}
