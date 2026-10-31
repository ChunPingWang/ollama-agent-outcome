package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 更新個人資料的應用層入口介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {

    /**
     * 根據請求內容更新指定的個人資料。
     *
     * @param request 包含新資料的請求 DTO
     */
    void update(UpdateProfileRequest request);

    /**
     * 用於接收更新資料的請求 DTO record。
     *
     * @param id          要更新的個人資料 ID
     * @param email       新的電子郵件地址
     * @param displayName 新的顯示名稱
     */
    record UpdateProfileRequest(UUID id, String email, String displayName) {}
}
