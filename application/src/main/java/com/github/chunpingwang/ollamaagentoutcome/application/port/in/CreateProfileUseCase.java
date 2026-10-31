package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.command.CreateProfileCommand;

/**
 * 新增個人資料的使用情境 Port。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的邏輯。
     *
     * @param command 包含建立所需的輸入資料
     */
    void execute(CreateProfileCommand command);
}
