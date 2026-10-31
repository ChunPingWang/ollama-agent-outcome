package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * 個人資料的回應 DTO。
 */
record ProfileResponse(
        UUID id,
        String email,
        String name,
        Integer age
) {
}
