package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 個人資料的回應 DTO。
 */
record ProfileResponse(
        String id,
        String email,
        String name,
        Integer age
) {
    /**
     * 從 Domain Profile 建立回應 DTO。
     *
     * @param profile 個人資料 Domain 物件
     */
    public ProfileResponse(Profile profile) {
        this(
                profile.id().getValue(),
                profile.email().getValue(),
                profile.name().getValue(),
                profile.age().getValue()
        );
    }
}
