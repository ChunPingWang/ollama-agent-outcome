package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 查詢單一個人資料的使用者情境介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼取得個人資料。若找不到則拋出 {@link com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException}。
     *
     * @param profileId 個人資料識別碼
     * @return 找到的個人資料
     */
    com.github.chunpingwang.ollamaagentoutcome.domain.Profile execute(String profileId);
}
