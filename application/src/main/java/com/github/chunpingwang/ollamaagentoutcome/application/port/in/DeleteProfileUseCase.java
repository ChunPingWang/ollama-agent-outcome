package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的使用者情境介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param profileId 要刪除的個人資料識別碼
     */
    void execute(String profileId);
}
