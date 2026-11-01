package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 儲存個人資料的外向 Port。
 */
public interface SaveProfilePort {
    /**
     * 儲存或更新個人資料。
     * @param profile 要儲存的個人資料實體
     * @return 儲存後的個人資料（通常與輸入相同）
     */
    Profile save(Profile profile);
}
