package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    void execute(CreateProfileRequest request);
}
