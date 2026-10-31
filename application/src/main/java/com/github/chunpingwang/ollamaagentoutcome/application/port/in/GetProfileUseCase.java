package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.dto.GetProfileResult;

/**
 * 查詢個人資料的應用層入口介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼查詢個人資料。
     *
     * @param id 個人資料識別碼
     * @return 包含資料內容的結果記錄
     */
    GetProfileResult execute(ProfileId id);
}
