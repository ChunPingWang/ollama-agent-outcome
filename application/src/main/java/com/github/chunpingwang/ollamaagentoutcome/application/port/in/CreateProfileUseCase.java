package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 新增個人資料的使用者情境介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param request 包含電子郵件與顯示名稱的請求資料
     */
    void execute(CreateProfileRequest request);

    /**
     * 新增個人資料的輸入參數記錄類別。
     *
     * @param email    電子郵件地址值物件
     * @param displayName 顯示名稱值物件（可為 null）
     */
    record CreateProfileRequest(Email email, DisplayName displayName) {}
}
