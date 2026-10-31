package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

/**
 * 查詢個人資料的指令物件。
 */
public record GetProfileQuery(String id) {
    public GetProfileQuery {
        Objects.requireNonNull(id, "id must not be null");
    }
}
