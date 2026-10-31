package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param command 包含更新資訊的指令
     * @return 更新後的個人資料
     */
    Profile execute(UpdateProfileCommand command);
}
