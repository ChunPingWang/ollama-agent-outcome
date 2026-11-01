package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據 ID 刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void execute(ProfileId id);
}
