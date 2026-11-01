package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的應用層入口介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param request 包含電子郵件與顯示名稱的建立請求
     */
    void execute(CreateProfileRequest request);

    /**
     * 建立請求的資料傳輸物件。
     */
    record CreateProfileRequest(String email, String displayName) {}
}
