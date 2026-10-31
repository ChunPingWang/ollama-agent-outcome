package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 更新個人資料的 Command 物件。
 */
public record UpdateProfileCommand(
        UUID profileId,
        String name,
        String email
) {
    public UpdateProfileCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
    }
}
