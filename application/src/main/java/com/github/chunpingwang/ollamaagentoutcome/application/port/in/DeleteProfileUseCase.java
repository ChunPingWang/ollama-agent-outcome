package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的使用案例介面 (Inbound Port)。
 */
public interface DeleteProfileUseCase {
    void execute(DeleteProfileCommand command);
}
