package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的入口介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param request 包含建立所需資訊的請求物件
     * @return 新建立個人資料的識別碼
     */
    ProfileId execute(CreateProfileRequest request);
}
