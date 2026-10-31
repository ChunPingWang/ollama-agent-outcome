package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.InvalidDisplayNameException;

/**
 * 顯示名稱 Value Object。
 */
public record DisplayName(String name) {

    private static final int MAX_LENGTH = 50;

    public DisplayName {
        if (name == null || name.isBlank()) {
            throw new InvalidDisplayNameException();
        }
        if (name.length() > MAX_LENGTH) {
            throw new InvalidDisplayNameException(MAX_LENGTH);
        }
    }

    /**
     * 建立新的 Display Name 實例（靜態工廠）。
     */
    public static DisplayName of(String rawName) {
        return new DisplayName(rawName.trim());
    }
}
