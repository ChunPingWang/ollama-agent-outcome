package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * 建立個人資料的請求 DTO，封裝輸入參數。
 */
public record CreateProfileRequest(Email email, DisplayName displayName) {
}
