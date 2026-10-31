package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.command.UpdateProfileCommand;

/**
 * 更新個人資料的使用情境 Port。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的邏輯。
     *
     * @param command 包含更新所需的輸入資料
     */
    void execute(UpdateProfileCommand command);
}
