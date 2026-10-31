package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;

/**
 * 個人資料的回應 DTO。
 */
record ProfileResponse(
    String id,
    String email,
    String name,
    String bio
) {
    /**
     * 從 Application 層的 Response 轉換而來。
     */
    ProfileResponse(GetProfileUseCase.ProfileResponse response) {
        this(response.id(), response.email(), response.name(), response.bio());
    }
}
