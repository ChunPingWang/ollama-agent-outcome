package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料實體。
 *
 * @param id    識別碼
 * @param name  姓名
 * @param email 電子郵件
 * @param note  備註
 */
public record Profile(
        ProfileId id,
        String name,
        String email,
        String note
) {

    /**
     * 建立個人資料，並驗證不變條件。
     *
     * @param id    識別碼
     * @param name  姓名
     * @param email 電子郵件
     * @param note  備註
     */
    public Profile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Email is invalid");
        }
    }

    /**
     * 檢查電子郵件格式是否合法。
     *
     * @param email 電子郵件
     * @return 是否合法
     */
    private static boolean isValidEmail(String email) {
        // 簡易的電子郵件格式檢查：包含 @ 且前後非空，且 @ 後有 .
        return email != null
                && email.contains("@")
                && email.indexOf("@") > 0
                && email.indexOf("@") < email.length() - 1
                && email.substring(email.indexOf("@") + 1).contains(".");
    }
}
