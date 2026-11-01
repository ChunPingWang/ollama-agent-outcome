package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的 Inbound Port。
 */
public interface UpdateProfileUseCase {
    void execute(UpdateProfileRequest request, String id);
}
