package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 簡介值物件。
 * 若為 null 則視為空字串。
 */
public final class Bio {
    private final String value;

    public Bio(String value) {
        this.value = value == null ? "" : value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bio bio = (Bio) o;
        return value.equals(bio.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
