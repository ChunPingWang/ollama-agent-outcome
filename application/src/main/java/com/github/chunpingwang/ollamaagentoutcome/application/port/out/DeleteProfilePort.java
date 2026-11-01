package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.UUID;

/**
 * 刪除個人資料的 Outbound Port 介面。
 */
public interface DeleteProfilePort {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void deleteById(UUID id);
}
