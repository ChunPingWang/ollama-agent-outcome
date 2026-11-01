package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 儲存個人資料的 Outbound Port。
 */
public interface SaveProfilePort {

    /**
     * 儲存或更新個人資料。
     *
     * @param profile 要儲存的個人資料實體
     */
    void save(Profile profile);
}
