package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的應用層入口介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務流程。
     *
     * @param request 包含電子郵件與顯示名稱的更新請求
     * @param id      要更新的個人資料識別碼
     */
    void execute(UpdateProfileRequest request, String id);

    /**
     * 更新個人資料所需的輸入資料 DTO。
     */
    record UpdateProfileRequest(String email, String displayName) {}
}
