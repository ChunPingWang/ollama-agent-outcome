package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的輸入請求記錄。
 */
public record UpdateProfileRequest(ProfileId id, DisplayName displayName) {
}
