package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

/**
 * 刪除個人資料的外向 Port 介面。
 */
public interface DeleteProfilePort {

    /**
     * 根據識別碼永久刪除個人資料。
     *
     * @param id 要刪除的個人資料唯一識別碼
     */
    void deleteById(String id);
}
