package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {
    void execute(String id);
}
