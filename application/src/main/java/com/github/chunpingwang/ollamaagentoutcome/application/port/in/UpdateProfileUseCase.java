package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的使用者情境介面 (Inbound Port)。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param command 包含更新所需資訊的命令物件
     */
    void execute(UpdateProfileCommand command);
}
