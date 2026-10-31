package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的指令。
 */
public record CreateProfileCommand(ProfileId id, String name, Email email) {
}
