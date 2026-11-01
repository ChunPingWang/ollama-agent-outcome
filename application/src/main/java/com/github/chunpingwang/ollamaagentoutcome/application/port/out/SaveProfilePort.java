package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 儲存個人資料的外向端口。
 */
public interface SaveProfilePort {
    /**
     * 將個人資料持久化。
     * @param profile 要儲存的個人資料
     * @return 儲存後的個人資料（可能包含資料庫產生的狀態）
     */
    Profile save(Profile profile);
}
