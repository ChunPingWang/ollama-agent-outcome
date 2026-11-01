package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;

import java.util.UUID;

/**
 * 儲存個人資料的外向 Port (Outbound Port)。
 */
public interface SaveProfilePort {

    /**
     * 檢查電子郵件是否已存在。
     *
     * @param email 要檢查的電子郵件
     * @return 若存在則回傳 true
     */
    boolean existsByEmail(Email email);

    /**
     * 儲存新的個人資料。
     *
     * @param id        個人資料識別碼
     * @param email     電子郵件
     * @param displayName 顯示名稱
     */
    void save(UUID id, Email email, DisplayName displayName);
}
