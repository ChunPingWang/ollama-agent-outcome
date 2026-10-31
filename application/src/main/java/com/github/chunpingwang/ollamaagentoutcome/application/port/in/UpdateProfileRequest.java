package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的請求資料傳輸物件。
 *
 * @param name  姓名
 * @param email 電子郵件
 * @param note  備註
 */
public record UpdateProfileRequest(
        String name,
        String email,
        String note
) {
}
