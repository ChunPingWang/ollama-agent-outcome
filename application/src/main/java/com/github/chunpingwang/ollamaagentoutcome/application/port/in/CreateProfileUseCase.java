package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 新增個人資料的入口介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param email      電子郵件地址
     * @param displayName 顯示名稱（可為 null）
     */
    void execute(Email email, DisplayName displayName);
}
