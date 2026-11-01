package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新顯示名稱的 Inbound Port。
 */
public interface UpdateProfileDisplayNameUseCase {

    /**
     * 執行更新顯示名稱的業務邏輯。
     *
     * @param id          個人資料識別碼
     * @param newDisplayName 新的顯示名稱
     */
    void execute(ProfileId id, DisplayName newDisplayName);
}
