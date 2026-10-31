package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileCommand command);
}
