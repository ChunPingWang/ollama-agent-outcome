package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 用於建立個人資料的命令記錄器。
 */
public record CreateProfileCommand(String email, String displayName) {}
