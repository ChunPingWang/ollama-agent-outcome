package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    /**
     * 執行建立個人資料的業務邏輯。
     * @param email 電子郵件地址
     * @param displayName 顯示名稱（可為 null）
     * @return 建立的 Profile ID
     */
    ProfileId execute(Email email, DisplayName displayName);
}
