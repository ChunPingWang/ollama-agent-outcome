package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的用例介面 (Inbound Port)。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的業務邏輯。
     *
     * @param id 個人資料識別碼
     */
    void execute(ProfileId id);
}
