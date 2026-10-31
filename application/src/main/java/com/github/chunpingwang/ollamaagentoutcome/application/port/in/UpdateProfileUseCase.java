package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的 Inbound Port (Use Case)。
 * 定義了更新個人資料的業務邏輯契約。
 */
public interface UpdateProfileUseCase {

    /**
     * 更新個人資料的姓名。
     *
     * @param id     識別碼
     * @param newName 新的姓名
     * @return 更新後的個人資料
     * @throws com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException 若找不到資料
     */
    Profile updateProfile(ProfileId id, String newName);
}
