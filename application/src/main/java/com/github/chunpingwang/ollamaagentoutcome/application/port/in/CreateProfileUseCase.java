package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的應用層入口介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param request 包含電子郵件與顯示名稱的請求物件
     */
    void execute(CreateProfileRequest request);

    /**
     * 用於接收輸入資料的簡單 DTO。
     */
    record CreateProfileRequest(String email, String displayName) {}
}
