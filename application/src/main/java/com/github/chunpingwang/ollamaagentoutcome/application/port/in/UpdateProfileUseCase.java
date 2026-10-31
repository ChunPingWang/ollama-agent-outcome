package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的應用層入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 全量更新指定的個人資料。
     * @param id 個人資料識別碼
     * @param email 新的電子郵件地址
     * @param displayName 新的顯示名稱
     */
    void update(String id, String email, String displayName);
}
