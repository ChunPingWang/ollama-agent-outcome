package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的應用層入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     * @param id 要更新的個人資料識別碼
     * @param email 新的電子郵件地址
     * @param displayName 新的顯示名稱
     */
    void execute(String id, String email, String displayName);
}
