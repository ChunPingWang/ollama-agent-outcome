package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {
    Profile execute(UpdateProfileCommand command);
}
