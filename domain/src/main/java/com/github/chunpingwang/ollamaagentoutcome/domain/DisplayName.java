package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件。允許 null 或空字串；若僅含空白字符則視為 null/空。
 */
public final class DisplayName {

    private static final String BLANK_REGEX = "^\\s*$";

    private final String name;

    public DisplayName(String name) {
        if (name != null && name.matches(BLANK_REGEX)) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
