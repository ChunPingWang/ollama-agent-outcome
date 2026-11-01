package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的外向 Port。
 */
public interface DeleteProfilePort {
    /**
     * 根據 ID 刪除個人資料。
     * @param id 個人資料識別碼
     */
    void deleteById(ProfileId id);
}
