package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件。
 * 若為空白字串則視為 null。
 */
public final class DisplayName {

    private final String name;

    public DisplayName(String name) {
        if (name == null || name.isBlank()) {
            this.name = null;
        } else {
            this.name = name.trim();
        }
    }

    /**
     * 取得顯示名稱。
     * @return 顯示名稱，可能為 null
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return java.util.Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name);
    }
}
