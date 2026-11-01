package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 移除個人資料的外向端口 (Outbound Port)。
 */
public interface RemoveProfilePort {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void deleteById(ProfileId id);
}
