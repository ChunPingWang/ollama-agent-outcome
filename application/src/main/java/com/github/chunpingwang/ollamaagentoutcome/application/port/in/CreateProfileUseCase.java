package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {

    /**
     * 建立新的個人資料。
     *
     * @param name 姓名
     * @param email 電子郵件
     * @return 新生成的個人資料識別碼
     */
    ProfileId create(String name, String email);
}
