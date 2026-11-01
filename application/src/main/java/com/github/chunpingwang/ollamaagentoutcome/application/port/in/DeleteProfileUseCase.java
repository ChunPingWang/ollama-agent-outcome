package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的入口介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void execute(ProfileId id);
}
