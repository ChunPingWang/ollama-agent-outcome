package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的使用案例介面。
 */
public interface LoadProfileUseCase {

    /**
     * 根據 ID 載入個人資料。
     * @param id 個人資料識別碼
     * @return 包含姓名與電子郵件的結果物件
     */
    ProfileResult execute(ProfileId id);
}
