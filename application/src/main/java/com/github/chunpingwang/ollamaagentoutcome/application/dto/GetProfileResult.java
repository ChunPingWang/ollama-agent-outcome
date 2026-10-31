package com.github.chunpingwang.ollamaagentoutcome.application.dto;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

/**
 * 查詢個人資料的結果記錄。
 *
 * @param id 個人資料識別碼
 * @param name 姓名值物件
 * @param email 電子郵件值物件
 */
public record GetProfileResult(
        ProfileId id,
        Name name,
        Email email) {
}
