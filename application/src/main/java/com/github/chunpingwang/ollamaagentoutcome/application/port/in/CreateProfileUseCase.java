package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 建立個人資料的 Use Case 介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param request 包含建立所需資訊的請求物件
     * @return 建立後的個人資料
     */
    Profile execute(CreateProfileRequest request);
}
