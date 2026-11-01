package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據 ID 刪除個人資料。
     * @param id 要刪除的個人資料識別碼
     */
    void execute(String id);
}
