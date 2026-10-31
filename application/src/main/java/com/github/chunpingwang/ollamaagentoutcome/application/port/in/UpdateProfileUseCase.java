package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param id          要更新的個人資料識別碼
     * @param email       新的電子郵件地址（不可為 null）
     * @param displayName 新的顯示名稱（可為 null 或空字串）
     */
    void execute(ProfileId id, Email email, DisplayName displayName);
}
