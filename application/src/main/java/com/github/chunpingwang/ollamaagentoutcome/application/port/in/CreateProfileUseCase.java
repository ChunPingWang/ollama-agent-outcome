package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的使用者情境介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的情境。
     *
     * @param request 包含姓名與電子郵件的請求物件
     */
    void execute(CreateProfileRequest request);

    /**
     * 新增個人資料所需的輸入資料 DTO。
     */
    record CreateProfileRequest(String name, String email) {}
}
