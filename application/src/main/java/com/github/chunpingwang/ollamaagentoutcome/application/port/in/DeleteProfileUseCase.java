package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的應用層入口介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的業務邏輯。
     * @param id 要刪除的個人資料識別碼
     */
    void execute(String id);
}
