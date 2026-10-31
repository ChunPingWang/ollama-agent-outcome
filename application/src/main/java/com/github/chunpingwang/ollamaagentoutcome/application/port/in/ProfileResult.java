package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;

/**
 * 載入個人資料後的結果物件。
 */
public record ProfileResult(Name name, Email email) {
}
