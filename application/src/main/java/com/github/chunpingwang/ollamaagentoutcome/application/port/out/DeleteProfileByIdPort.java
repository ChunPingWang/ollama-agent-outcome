package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

/**
 * 刪除個人資料的外部端口介面。
 */
public interface DeleteProfileByIdPort {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param profileId 要刪除的個人資料唯一識別碼
     */
    void deleteById(String profileId);
}
