package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 刪除個人資料的 Command 物件。
 */
public record DeleteProfileCommand(
        UUID profileId
) {
    public DeleteProfileCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
    }
}
