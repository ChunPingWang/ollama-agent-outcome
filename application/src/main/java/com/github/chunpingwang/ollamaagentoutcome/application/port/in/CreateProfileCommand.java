package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;

/**
 * 新增個人資料的命令物件。
 */
public record CreateProfileCommand(Name name, Email email) {
}
