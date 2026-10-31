package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的使用案例介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的操作。
     * @param command 包含識別碼、新姓名與新電子郵件的命令物件
     */
    void execute(UpdateProfileCommand command);
}
