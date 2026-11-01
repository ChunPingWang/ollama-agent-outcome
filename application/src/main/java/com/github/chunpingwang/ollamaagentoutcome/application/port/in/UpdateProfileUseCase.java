package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的使用案例入口。
 */
public interface UpdateProfileUseCase {
    /**
     * 執行更新個人資料的業務邏輯。
     * @param id 個人資料識別碼
     * @param email 新的電子郵件地址
     * @param displayName 新的顯示名稱
     */
    void execute(ProfileId id, String email, String displayName);
}
