package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    void execute(Email email, DisplayName displayName);
}
