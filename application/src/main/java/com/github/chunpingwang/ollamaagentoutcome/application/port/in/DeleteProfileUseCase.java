package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {
    void execute(DeleteProfileCommand command);
}
