package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的 Inbound Port。
 */
public interface DeleteProfileUseCase {
    void execute(ProfileId id);
}
