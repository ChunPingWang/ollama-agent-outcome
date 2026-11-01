package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;

/**
 * 載入個人資料的 Outbound Port。
 */
public interface LoadProfilePort {
    Optional<Profile> findById(String id);
}
