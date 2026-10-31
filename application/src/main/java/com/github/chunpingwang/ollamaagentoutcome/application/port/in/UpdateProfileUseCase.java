package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 更新個人資料的使用案例介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的操作。
     *
     * @param id 個人資料識別碼
     * @param name 新姓名
     * @param email 新電子郵件地址
     */
    void execute(UUID id, String name, String email);
}
