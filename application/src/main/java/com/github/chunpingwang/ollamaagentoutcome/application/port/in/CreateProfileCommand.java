package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 用於建立個人資料的指令記錄。
 *
 * @param email      使用者的電子郵件地址
 * @param displayName 使用者希望顯示的名稱
 * @param bio        個人簡介（可選）
 */
public record CreateProfileCommand(String email, String displayName, String bio) {
}
