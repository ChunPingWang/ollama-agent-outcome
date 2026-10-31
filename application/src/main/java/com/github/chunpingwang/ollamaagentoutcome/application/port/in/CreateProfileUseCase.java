package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {

    /**
     * 建立新的個人資料。
     *
     * @param request 包含建立所需資訊的請求物件
     * @return 建立後的個人資料識別碼
     */
    ProfileId create(CreateProfileRequest request);

    /**
     * 建立個人資料的請求 DTO。
     */
    record CreateProfileRequest(String email, String name, Integer age) {
    }
}
