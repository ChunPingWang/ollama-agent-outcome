package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param command 包含建立所需資訊的指令
     * @return 建立後的個人資料
     */
    Profile execute(CreateProfileCommand command);
}
