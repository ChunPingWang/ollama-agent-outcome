package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的使用案例入口。
 */
public interface DeleteProfileUseCase {
    /**
     * 根據 ID 刪除個人資料。
     * @param id 要刪除的個人資料識別碼
     */
    void execute(ProfileId id);
}
