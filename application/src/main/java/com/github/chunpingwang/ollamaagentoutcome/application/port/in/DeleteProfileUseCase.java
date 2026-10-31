package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的使用者情境介面 (Inbound Port)。
 */
public interface DeleteProfileUseCase {

    /**
     * 執行刪除個人資料的業務邏輯。
     *
     * @param command 包含刪除所需資訊的命令物件
     */
    void execute(DeleteProfileCommand command);
}
