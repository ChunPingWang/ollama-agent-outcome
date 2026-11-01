package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 新增個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param email      電子郵件地址
     * @param displayName 顯示名稱
     * @return 新建立的 Profile ID
     */
    ProfileId execute(String email, String displayName);
}
