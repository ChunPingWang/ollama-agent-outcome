package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的應用層入口介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param request 包含新電子郵件與顯示名稱的請求物件
     * @param id      要更新的個人資料識別碼
     */
    void execute(UpdateRequest request, String id);

    /**
     * 用於接收輸入資料的簡單 DTO。
     */
    record UpdateRequest(String email, String displayName) {}
}
