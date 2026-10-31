package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 新增個人資料的應用層入口介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {

    /**
     * 根據請求內容建立新的個人資料。
     *
     * @param request 包含電子郵件與顯示名稱的請求 DTO
     */
    void create(CreateProfileRequest request);

    /**
     * 用於接收新資料建立的請求 DTO record。
     *
     * @param email       電子郵件地址
     * @param displayName 顯示名稱
     */
    record CreateProfileRequest(String email, String displayName) {}
}
