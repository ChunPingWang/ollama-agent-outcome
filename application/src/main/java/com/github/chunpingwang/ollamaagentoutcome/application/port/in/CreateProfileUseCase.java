package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;

/**
 * 新增個人資料的使用情境介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param command 包含姓名、電子郵件與簡介的命令物件
     */
    void execute(CreateProfileCommand command);
}
