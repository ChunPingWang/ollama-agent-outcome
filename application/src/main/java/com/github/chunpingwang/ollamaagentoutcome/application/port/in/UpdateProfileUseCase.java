package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的使用者情境介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param request 包含識別碼與新顯示名稱的請求資料
     */
    void execute(UpdateProfileRequest request);

    /**
     * 更新個人資料的輸入參數記錄類別。
     *
     * @param id          要更新的個人資料識別碼
     * @param displayName 新的顯示名稱值物件（可為 null）
     */
    record UpdateProfileRequest(ProfileId id, DisplayName displayName) {}
}
