package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 根據 ID 刪除個人資料的外向端口。
 */
public interface DeleteProfileByIdPort {
    /**
     * 根據識別碼刪除個人資料。
     * @param id 要刪除的個人資料識別碼
     */
    void deleteById(ProfileId id);
}
