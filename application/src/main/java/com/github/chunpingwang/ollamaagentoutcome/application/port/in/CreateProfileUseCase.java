package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Inbound Port (Use Case)。
 * 定義了建立新個人資料的業務邏輯契約。
 */
public interface CreateProfileUseCase {

    /**
     * 建立新的個人資料。
     *
     * @param name  姓名
     * @param email 電子郵件
     * @return 新生成的識別碼
     */
    ProfileId createProfile(String name, String email);
}
