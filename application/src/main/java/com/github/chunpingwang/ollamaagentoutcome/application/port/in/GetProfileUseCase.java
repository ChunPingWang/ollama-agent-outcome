package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢單一個人資料的應用層入口介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼查詢個人資料。
     * @param id 個人資料識別碼
     * @return 找到的個人資料，若不存在則回傳空 Optional
     */
    Optional<Profile> execute(String id);
}
