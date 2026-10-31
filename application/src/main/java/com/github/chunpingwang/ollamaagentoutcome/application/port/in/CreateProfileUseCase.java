package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 新增個人資料的應用層入口介面。
 */
public interface CreateProfileUseCase {

    /**
     * 根據提供的資訊建立新的個人資料。
     * @param email 電子郵件地址
     * @param displayName 顯示名稱
     * @return 新建立的個人資料識別碼
     */
    ProfileId create(String email, String displayName);
}
