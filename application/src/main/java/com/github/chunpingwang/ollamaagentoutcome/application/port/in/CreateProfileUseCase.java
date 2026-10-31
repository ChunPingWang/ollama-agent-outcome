package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.command.CreateProfileCommand;

/**
 * 新增個人資料的使用情境埠。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的操作。
     *
     * @param command 包含建立所需資訊的命令物件
     */
    void execute(CreateProfileCommand command);
}
