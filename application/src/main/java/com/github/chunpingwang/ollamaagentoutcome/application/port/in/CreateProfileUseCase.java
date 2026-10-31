package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * 新增個人資料的使用者情境介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的情境。
     *
     * @param command 包含電子郵件、顯示名稱與簡介的命令物件
     */
    void execute(CreateProfileCommand command);
}
