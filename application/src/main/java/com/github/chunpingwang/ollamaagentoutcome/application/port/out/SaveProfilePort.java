package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 儲存個人資料的 Outbound Port 介面。
 */
public interface SaveProfilePort {

    /**
     * 將個人資料存入持久層。
     *
     * @param profile 要儲存的個人資料實體
     */
    void save(Profile profile);
}
