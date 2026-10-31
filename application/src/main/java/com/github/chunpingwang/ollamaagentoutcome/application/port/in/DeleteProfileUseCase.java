package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的應用層 Port 介面。
 */
public interface DeleteProfileUseCase {
    /**
     * 根據 ID 刪除個人資料。
     *
     * @param profileId 個人資料 ID
     */
    void delete(String profileId);
}
