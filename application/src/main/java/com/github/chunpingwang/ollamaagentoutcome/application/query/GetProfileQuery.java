package com.github.chunpingwang.ollamaagentoutcome.application.query;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 用於查詢個人資料的查詢物件。
 */
public record GetProfileQuery(ProfileId profileId) {
}
