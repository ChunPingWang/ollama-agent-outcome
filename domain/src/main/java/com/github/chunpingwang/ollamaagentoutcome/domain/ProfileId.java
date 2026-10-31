package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(String value) {

    /**
     * 建立新的識別碼。
     *
     * @param value UUID 字串
     * @throws IllegalArgumentException 當 value 為空或空白時
     */
    public ProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("不能為空");
        }
    }
}
