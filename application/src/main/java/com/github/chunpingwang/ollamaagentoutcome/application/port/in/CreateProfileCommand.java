package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 用於傳遞建立個人資料請求的資料物件。
 */
public record CreateProfileCommand(String name, String email) {
    public CreateProfileCommand {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
    }
}
