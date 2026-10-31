package com.github.chunpingwang.ollamaagentoutcome.application.command;

/**
 * 用於建立個人資料的命令物件。
 */
public record CreateProfileCommand(
        String email,
        String displayName,
        String bio) {
}
