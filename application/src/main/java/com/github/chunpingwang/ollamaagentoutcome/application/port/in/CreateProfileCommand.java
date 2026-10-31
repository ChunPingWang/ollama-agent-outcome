package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 建立個人資料的命令 DTO。
 */
public record CreateProfileCommand(String email, String displayName, String bio) {

    public CreateProfileCommand {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(displayName, "Display name cannot be null");
    }
}
