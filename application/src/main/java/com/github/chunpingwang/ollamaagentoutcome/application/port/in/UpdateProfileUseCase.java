package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 更新個人資料的 Use Case 介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param profileId 個人資料 ID
     * @param request 包含更新所需資訊的請求物件
     * @return 更新後的個人資料
     */
    Profile execute(String profileId, UpdateProfileRequest request);
}
