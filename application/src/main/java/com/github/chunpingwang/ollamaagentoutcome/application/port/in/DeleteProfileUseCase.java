package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的 Use Case 介面。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的業務邏輯。
     *
     * @param profileId 個人資料 ID
     */
    void execute(String profileId);
}
