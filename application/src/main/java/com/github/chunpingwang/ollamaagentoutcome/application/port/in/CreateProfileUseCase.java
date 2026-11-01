package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的應用層入口介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務流程。
     *
     * @param request 包含電子郵件與顯示名稱的建立請求
     */
    void execute(CreateProfileRequest request);

    /**
     * 建立個人資料所需的輸入資料 DTO。
     */
    record CreateProfileRequest(String email, String displayName) {}
}
