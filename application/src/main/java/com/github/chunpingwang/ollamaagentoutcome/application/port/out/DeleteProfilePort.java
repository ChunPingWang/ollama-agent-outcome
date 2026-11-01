package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的 Outbound Port。
 */
public interface DeleteProfilePort {
    /**
     * 根據識別碼刪除資料。
     * @param id 要刪除的個人資料識別碼
     */
    void deleteById(ProfileId id);
}
