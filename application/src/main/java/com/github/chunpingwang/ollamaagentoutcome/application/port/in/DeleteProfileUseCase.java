package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 刪除個人資料的使用情境 Port。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的邏輯。
     *
     * @param id 要刪除的個人資料識別碼
     */
    void execute(ProfileId id);
}
