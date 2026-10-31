package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的使用案例介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileCommand command);
}
