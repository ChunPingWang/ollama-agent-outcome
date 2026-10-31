package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的使用者情境介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的情境。
     *
     * @param request 包含識別碼、新姓名與新電子郵件的請求物件
     */
    void execute(UpdateProfileRequest request);

    /**
     * 更新個人資料所需的輸入資料 DTO。
     */
    record UpdateProfileRequest(String id, String name, String email) {}
}
