package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.query.GetProfileQuery;

/**
 * 查詢單一個人資料的使用情境埠。
 */
public interface GetProfileUseCase {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param query 包含目標 ID 的查詢物件
     * @return 找到的個人資料，若不存在則回傳 Optional.empty()
     */
    Optional<Profile> execute(GetProfileQuery query);
}
