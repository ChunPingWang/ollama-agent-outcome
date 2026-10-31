package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;

/**
 * 更新個人資料的命令 DTO。
 */
public record UpdateProfileCommand(ProfileId id, String email, String displayName, String bio) {

    public UpdateProfileCommand {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(displayName, "Display name cannot be null");
    }
}
