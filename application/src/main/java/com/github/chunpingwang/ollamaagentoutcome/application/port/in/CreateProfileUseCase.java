package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 建立個人資料的使用者情境介面 (Inbound Port)。
 */
public interface CreateProfileUseCase {

    /**
     * 執行建立個人資料的業務邏輯。
     *
     * @param command 包含建立所需資訊的命令物件
     */
    void execute(CreateProfileCommand command);
}
