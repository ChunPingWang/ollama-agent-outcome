package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 用於更新個人資料的命令記錄器。
 */
public record UpdateProfileCommand(String id, String email, String displayName) {}
