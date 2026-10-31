package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 查詢個人資料的應用層入口介面 (Inbound Port)。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 取得個人資料資訊。
     *
     * @param id 個人資料的唯一識別碼
     * @return 包含個人資料詳細資訊的回應 DTO
     */
    GetProfileResponse get(UUID id);

    /**
     * 用於回傳查詢結果的 Response DTO record。
     *
     * @param id          個人資料唯一識別碼
     * @param email       電子郵件地址
     * @param displayName 顯示名稱
     */
    record GetProfileResponse(UUID id, String email, String displayName) {}
}
