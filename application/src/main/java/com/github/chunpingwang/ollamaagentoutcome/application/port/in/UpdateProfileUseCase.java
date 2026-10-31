package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 更新個人資料。
     *
     * @param id    個人資料識別碼
     * @param request 包含更新所需資訊的請求物件
     * @return 更新後的個人資料
     */
    Profile updateProfile(ProfileId id, UpdateProfileRequest request);

    /**
     * 更新個人資料的請求 DTO。
     */
    record UpdateProfileRequest(String email, String name, Integer age) {
    }
}
