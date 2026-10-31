package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的使用案例介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileCommand command);
}
