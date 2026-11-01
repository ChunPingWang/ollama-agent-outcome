package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {
    /**
     * 執行更新個人資料的流程。
     * @param id 個人資料識別碼
     * @param email 新的電子郵件地址
     * @param displayName 新的顯示名稱（可為 null）
     */
    void execute(ProfileId id, Email email, DisplayName displayName);
}
