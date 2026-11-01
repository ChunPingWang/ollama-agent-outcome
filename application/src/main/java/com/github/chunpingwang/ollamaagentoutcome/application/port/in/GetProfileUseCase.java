package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * 查詢個人資料的 Inbound Port。
 */
public interface GetProfileUseCase {
    Optional<Profile> execute(ProfileId id);
}
