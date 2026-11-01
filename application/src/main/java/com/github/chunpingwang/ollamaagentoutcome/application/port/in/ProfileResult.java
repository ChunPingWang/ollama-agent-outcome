package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的回應記錄。
 */
public record ProfileResult(ProfileId id, Email email, DisplayName displayName) {
}
