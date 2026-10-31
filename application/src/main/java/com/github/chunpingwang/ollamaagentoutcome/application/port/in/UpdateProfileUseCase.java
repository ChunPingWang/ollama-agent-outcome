package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param id    個人資料識別碼
     * @param request 更新請求
     * @return 更新後的個人資料
     */
    Profile execute(ProfileId id, UpdateProfileRequest request);
}
