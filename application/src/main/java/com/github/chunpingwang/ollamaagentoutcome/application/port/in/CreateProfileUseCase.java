package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param request 建立請求
     * @return 新建立的個人資料識別碼
     */
    ProfileId execute(CreateProfileRequest request);
}
