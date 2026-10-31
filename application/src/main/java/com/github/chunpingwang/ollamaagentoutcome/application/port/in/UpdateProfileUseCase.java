package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.command.UpdateProfileCommand;

/**
 * 更新個人資料的使用情境埠。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的操作。
     *
     * @param command 包含更新資訊的命令物件
     */
    void execute(UpdateProfileCommand command);
}
