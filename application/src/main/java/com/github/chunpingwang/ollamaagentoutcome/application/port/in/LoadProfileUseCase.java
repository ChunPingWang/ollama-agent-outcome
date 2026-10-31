package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的 Inbound Port (Use Case)。
 * 定義了根據識別碼載入個人資料的業務邏輯契約。
 */
public interface LoadProfileUseCase {

    /**
     * 根據識別碼載入個人資料。
     *
     * @param id 識別碼
     * @return 個人資料
     * @throws com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException 若找不到資料
     */
    Profile loadProfile(ProfileId id);
}
