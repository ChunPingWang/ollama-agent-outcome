package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 更新個人資料。
     *
     * @param id 個人資料識別碼
     * @param name 新的姓名
     * @param email 新的電子郵件地址
     */
    void update(java.util.UUID id, String name, String email);
}
