package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 刪除個人資料的指令物件。
 */
public record DeleteProfileCommand(String id) {
    public DeleteProfileCommand {
        Objects.requireNonNull(id, "id must not be null");
    }
}
