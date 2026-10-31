package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料的唯一識別碼 Value Object。
 * 確保識別碼的型別安全與不可變性。
 */
public final class ProfileId {

    private final String value;

    /**
     * 建立新的識別碼。
     *
     * @param value 識別碼字串
     * @throws IllegalArgumentException 若 value 為 null 或空白
     */
    public ProfileId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value must not be null or blank");
        }
        this.value = value;
    }

    /**
     * 取得識別碼的值。
     *
     * @return 識別碼字串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileId profileId = (ProfileId) o;
        return value.equals(profileId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
