package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 儲存個人資料的 Outbound Port。
 */
public interface SaveProfilePort {
    Profile save(Profile profile);
}
