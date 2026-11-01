package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的應用層入口介面 (Inbound Port)。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據識別碼永久刪除個人資料。
     *
     * @param id 要刪除的個人資料唯一識別碼
     */
    void execute(String id);
}
