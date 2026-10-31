package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;

/**
 * 更新個人資料的使用情境介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param command 包含 ID、姓名、電子郵件與簡介的命令物件
     */
    void execute(UpdateProfileCommand command);
}
