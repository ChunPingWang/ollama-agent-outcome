package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 使用者姓名值物件，限制長度與非空條件。
 */
public record Name(String text) {

    private static final int MAX_LENGTH = 50;

    /**
     * 建立有效的姓名。
     *
     * @param text 姓名內容
     * @throws InvalidNameException 當長度不合規或為空白時
     */
    public Name {
        if (text == null || text.isBlank() || text.length() > MAX_LENGTH) {
            throw new InvalidNameException("長度必須在 1 到 " + MAX_LENGTH + " 之間");
        }
    }
}
