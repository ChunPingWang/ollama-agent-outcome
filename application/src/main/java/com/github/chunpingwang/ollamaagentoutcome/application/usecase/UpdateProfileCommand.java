package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * 用於更新個人資料的命令物件。
 */
public record UpdateProfileCommand(UUID id, String name, String email, String bio) {
}
