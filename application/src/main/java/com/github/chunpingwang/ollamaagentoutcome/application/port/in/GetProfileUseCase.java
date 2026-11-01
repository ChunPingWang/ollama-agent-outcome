package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢單一個人資料的使用案例入口。
 */
public interface GetProfileUseCase {
    /**
     * 根據 ID 查詢個人資料。
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則拋出例外或回傳 Optional（依實作而定）
     */
    Profile findById(ProfileId id);
}
