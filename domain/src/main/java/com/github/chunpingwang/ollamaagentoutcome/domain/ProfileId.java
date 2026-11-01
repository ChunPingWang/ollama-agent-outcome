package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料的唯一識別碼值物件，基於 UUID String。
 */
public record ProfileId(String value) {

    /**
     * 建立新的 ProfileId。
     *
     * @param value UUID 字串
     * @throws IllegalArgumentException 如果 value 為 null、空白或格式不正確
     */
    public ProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("must not be blank");
        }
        // 驗證 UUID 格式
        try {
            java.util.UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format", e);
        }
        value = value.trim();
    }
}
