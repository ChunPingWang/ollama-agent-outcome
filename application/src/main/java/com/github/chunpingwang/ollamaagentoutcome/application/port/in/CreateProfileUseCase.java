package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 新增個人資料的應用層入口介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的業務邏輯。
     *
     * @param command 包含電子郵件與顯示名稱的命令物件
     * @return 成功建立的 Profile 實體
     */
    Profile execute(CreateProfileCommand command);
}
