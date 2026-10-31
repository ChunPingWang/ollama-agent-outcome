package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 個人資料的回應 DTO。
 *
 * @param id    識別碼
 * @param name  姓名
 * @param email 電子郵件
 * @param note  備註
 */
record ProfileResponse(
        String id,
        String name,
        String email,
        String note
) {

    /**
     * 從 Domain Profile 建立 Response DTO。
     *
     * @param profile 領域實體
     * @return 回應 DTO
     */
    static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.id().value().toString(),
                profile.name(),
                profile.email(),
                profile.note()
        );
    }
}
