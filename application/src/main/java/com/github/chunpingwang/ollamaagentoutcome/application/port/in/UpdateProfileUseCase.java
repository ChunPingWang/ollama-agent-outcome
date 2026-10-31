package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的使用者情境介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的情境（全量替換）。
     *
     * @param command 包含 ID、電子郵件、顯示名稱與簡介的命令物件
     */
    void execute(UpdateProfileCommand command);
}
