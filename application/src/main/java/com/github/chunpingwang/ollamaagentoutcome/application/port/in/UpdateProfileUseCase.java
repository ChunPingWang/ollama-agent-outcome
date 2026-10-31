package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileCommand command);
}
