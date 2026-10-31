package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param id    個人資料識別碼
     * @param request 包含更新所需資訊的請求物件
     * @return 更新後的個人資料實體
     */
    Profile execute(ProfileId id, UpdateProfileRequest request);
}
