package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的入站 Port 介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 執行查詢個人資料的業務邏輯。
     *
     * @param id 個人資料的唯一識別碼
     * @return 包含個人資料詳細資訊的結果物件
     */
    ProfileResult execute(ProfileId id);
}
