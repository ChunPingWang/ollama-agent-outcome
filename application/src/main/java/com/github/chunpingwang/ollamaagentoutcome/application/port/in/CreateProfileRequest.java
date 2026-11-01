package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 新增個人資料的輸入請求記錄。
 */
public record CreateProfileRequest(Email email, DisplayName displayName) {
}
