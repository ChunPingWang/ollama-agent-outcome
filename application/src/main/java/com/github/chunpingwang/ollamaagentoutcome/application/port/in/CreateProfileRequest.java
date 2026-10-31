package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 建立個人資料的請求資料傳輸物件。
 *
 * @param name  姓名
 * @param email 電子郵件
 * @param note  備註
 */
public record CreateProfileRequest(
        String name,
        String email,
        String note
) {
}
