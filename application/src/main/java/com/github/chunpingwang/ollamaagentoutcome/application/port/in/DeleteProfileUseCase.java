package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的 Inbound Port (Use Case)。
 * 定義了刪除個人資料的業務邏輯契約。
 */
public interface DeleteProfileUseCase {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 識別碼
     * @throws com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException 若找不到資料
     */
    void deleteProfile(ProfileId id);
}
