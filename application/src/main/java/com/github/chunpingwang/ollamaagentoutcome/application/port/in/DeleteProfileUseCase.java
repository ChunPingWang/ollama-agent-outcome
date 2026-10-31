package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的使用者情境介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 要刪除的個人資料唯一識別碼
     */
    void execute(ProfileId id);
}
