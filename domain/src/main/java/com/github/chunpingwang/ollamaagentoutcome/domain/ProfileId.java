package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼值物件，包裝 UUID。
 */
public final class ProfileId {

    private final UUID id;

    /**
     * 建立識別碼。
     *
     * @param id UUID，不可為 null
     * @throws IllegalArgumentException 若 id 為 null
     */
    public ProfileId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        this.id = id;
    }

    /**
     * 取得 UUID 內容。
     *
     * @return UUID
     */
    public UUID id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
