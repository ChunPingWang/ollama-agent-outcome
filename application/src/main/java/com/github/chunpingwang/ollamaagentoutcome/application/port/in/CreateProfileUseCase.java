package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的 Inbound Port 介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param email 電子郵件地址
     * @param displayName 顯示名稱
     */
    void execute(String email, String displayName);
}
