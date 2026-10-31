package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 查詢個人資料的 Command 物件。
 */
public record GetProfileCommand(
        UUID profileId
) {
    public GetProfileCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
    }
}
