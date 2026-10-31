package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;

/**
 * 刪除個人資料的命令 DTO。
 */
public record DeleteProfileCommand(ProfileId id) {

    public DeleteProfileCommand {
        Objects.requireNonNull(id, "ID cannot be null");
    }
}
