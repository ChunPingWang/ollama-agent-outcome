package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;

/**
 * 載入個人資料的外向 Port 介面。
 */
public interface LoadProfilePort {

    /**
     * 根據識別碼查詢個人資料。
     *
     * @param id 個人資料的唯一識別碼
     * @return 找到的個人資料，若不存在則回傳空 Optional
     */
    Optional<Profile> findById(String id);
}
