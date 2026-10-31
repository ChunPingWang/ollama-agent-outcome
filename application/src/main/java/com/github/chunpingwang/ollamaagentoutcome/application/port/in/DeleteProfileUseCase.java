package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;

/**
 * 刪除個人資料的使用情境介面 (Inbound Port)。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的業務邏輯。
     *
     * @param command 包含目標 ID 的命令物件
     */
    void execute(DeleteProfileCommand command);
}
