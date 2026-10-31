package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {

    /**
     * 刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void delete(java.util.UUID id);
}
