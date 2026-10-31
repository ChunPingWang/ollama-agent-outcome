package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的使用者情境介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼查詢個人資料並回傳結果。
     *
     * @param id 個人資料的唯一識別碼
     * @return 包含個人資料詳細資訊的結果記錄
     */
    ProfileResult execute(ProfileId id);

    /**
     * 查詢結果的記錄類別，封裝從領域層取得的資料。
     *
     * @param id        個人資料識別碼
     * @param email     電子郵件地址
     * @param displayName 顯示名稱
     */
    record ProfileResult(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId id,
                         com.github.chunpingwang.ollamaagentoutcome.domain.Email email,
                         com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName displayName) {}
}
