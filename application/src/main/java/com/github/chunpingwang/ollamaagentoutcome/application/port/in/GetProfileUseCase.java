package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.ProfileResult;

/**
 * 查詢單一個人資料的使用情境介面 (Inbound Port)。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param query 包含目標 ID 的查詢物件
     * @return 查詢結果 DTO
     */
    ProfileResult execute(GetProfileQuery query);
}
