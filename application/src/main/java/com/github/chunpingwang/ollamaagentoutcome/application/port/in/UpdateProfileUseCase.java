package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 更新個人資料的應用層入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param command 包含識別碼、新電子郵件與顯示名稱的命令物件
     * @return 成功更新的 Profile 實體
     */
    Profile execute(UpdateProfileCommand command);
}
