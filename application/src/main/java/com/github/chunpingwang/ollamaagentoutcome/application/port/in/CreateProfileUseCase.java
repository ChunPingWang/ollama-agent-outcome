package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 建立個人資料的 Inbound Port。
 */
public interface CreateProfileUseCase {
    Profile execute(Email email, DisplayName displayName);
}
