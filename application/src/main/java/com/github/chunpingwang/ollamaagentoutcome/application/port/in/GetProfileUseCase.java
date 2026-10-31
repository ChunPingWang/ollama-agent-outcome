package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.application.dto.ProfileResponseDTO;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 查詢個人資料的使用情境 Port。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param id 個人資料的唯一識別碼
     * @return 個人資料回應 DTO
     */
    ProfileResponseDTO execute(ProfileId id);
}
