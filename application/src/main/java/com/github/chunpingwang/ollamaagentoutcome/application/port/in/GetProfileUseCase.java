package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢個人資料的 Inbound Port。
 */
public interface GetProfileUseCase {
    Profile execute(GetProfileCommand command);
}
