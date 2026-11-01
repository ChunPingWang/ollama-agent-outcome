package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當查詢不存在的個人資料 ID 時拋出的領域例外。
 */
public class ProfileNotFoundException extends RuntimeException {
    /**
     * 根據 UUID 建立例外。
     *
     * @param id 找不到的識別碼
     */
    public ProfileNotFoundException(UUID id) {
        super("profile not found with id: " + id);
    }
    
    /**
     * 根據自訂訊息建立例外。
     *
     * @param message 錯誤訊息
     */
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
