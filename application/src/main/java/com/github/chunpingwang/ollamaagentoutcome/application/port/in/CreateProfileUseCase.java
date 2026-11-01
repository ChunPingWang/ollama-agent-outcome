package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的使用案例介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     * @param request 包含電子郵件與顯示名稱的請求物件
     */
    void execute(CreateProfileRequest request);
}
