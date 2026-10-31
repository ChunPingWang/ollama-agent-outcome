package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;

/**
 * 查詢個人資料的使用者情境介面 (Inbound Port)。
 */
public interface GetProfileUseCase {

    /**
     * 執行查詢個人資料的業務邏輯。
     *
     * @param query 包含查詢條件的查詢物件
     * @return 個人資料回應 DTO
     */
    ProfileResponse execute(GetProfileQuery query);
}
