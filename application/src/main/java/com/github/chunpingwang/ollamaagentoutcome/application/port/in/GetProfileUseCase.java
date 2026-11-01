package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢單一個人資料的使用案例介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料結果。
     * @param id 個人資料識別碼
     * @return 查詢結果記錄
     */
    ProfileResult execute(ProfileId id);
}
