package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

/**
 * 更新個人資料的應用層入口介面。
 */
public interface UpdateProfileUseCase {

    /**
     * 執行更新個人資料的業務邏輯。
     *
     * @param id 個人資料識別碼
     * @param name 新的姓名值物件
     * @param email 新的電子郵件值物件
     */
    void execute(ProfileId id, Name name, Email email);
}
