package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.Optional;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 載入個人資料的外部端口介面。
 */
public interface LoadProfilePort {

    /**
     * 根據識別碼載入個人資料。
     *
     * @param id 個人資料的唯一識別碼
     * @return 找到的資料，若不存在則回傳空 Optional
     */
    Optional<Profile> findById(String id);
}
