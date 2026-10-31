package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的指令。
 */
public record GetProfileCommand(ProfileId id) {
}
