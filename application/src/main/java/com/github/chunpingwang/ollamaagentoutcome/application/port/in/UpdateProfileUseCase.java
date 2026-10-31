package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 更新個人資料。
     *
     * @param id 個人資料識別碼
     * @param name 新姓名
     * @param email 新電子郵件
     */
    void update(ProfileId id, String name, String email);
}
