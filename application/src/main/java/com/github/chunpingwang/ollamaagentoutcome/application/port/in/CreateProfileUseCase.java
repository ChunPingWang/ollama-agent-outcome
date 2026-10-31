package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 新增個人資料的使用案例介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的操作。
     * @param command 包含姓名與電子郵件的命令物件
     * @return 新建立的個人資料識別碼
     */
    ProfileId execute(CreateProfileCommand command);
}
