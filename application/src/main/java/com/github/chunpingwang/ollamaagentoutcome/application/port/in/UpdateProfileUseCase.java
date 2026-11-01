package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新顯示名稱的業務邏輯。
     *
     * @param id          個人資料識別碼
     * @param displayName 新的顯示名稱（可為 null）
     */
    void execute(ProfileId id, DisplayName displayName);
}
