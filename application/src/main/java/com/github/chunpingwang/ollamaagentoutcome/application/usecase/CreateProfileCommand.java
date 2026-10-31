package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

/**
 * 用於建立個人資料的命令物件。
 */
public record CreateProfileCommand(String name, String email, String bio) {
}
