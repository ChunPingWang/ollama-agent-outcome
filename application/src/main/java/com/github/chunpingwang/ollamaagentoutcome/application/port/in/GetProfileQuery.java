package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Objects;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;

/**
 * 查詢個人資料的查詢 DTO。
 */
public record GetProfileQuery(ProfileId id) {

    public GetProfileQuery {
        Objects.requireNonNull(id, "ID cannot be null");
    }
}
