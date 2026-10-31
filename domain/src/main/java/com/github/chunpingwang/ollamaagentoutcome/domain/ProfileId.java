package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 個人資料的唯一識別碼值物件，基於 UUID String。
 */
public final class ProfileId {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final String value;

    public ProfileId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID 不能為 null");
        }
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("無效的 UUID 格式");
        }
        this.value = value;
    }

    /**
     * 取得底層 UUID 字串表示。
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
