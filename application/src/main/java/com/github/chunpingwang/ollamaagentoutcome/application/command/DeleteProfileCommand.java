package com.github.chunpingwang.ollamaagentoutcome.application.command;

import java.util.UUID;

/**
 * 用於刪除個人資料的命令物件。
 */
public record DeleteProfileCommand(UUID id) {
}
