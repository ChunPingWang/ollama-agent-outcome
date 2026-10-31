package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢個人資料的 Use Case 介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param profileId 個人資料 ID
     * @return 找到的個人資料
     */
    Profile execute(String profileId);
}
