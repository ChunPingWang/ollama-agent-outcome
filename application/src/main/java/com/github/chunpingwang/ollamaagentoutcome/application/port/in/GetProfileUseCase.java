package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 查詢個人資料的使用案例介面 (Inbound Port)。
 */
public interface GetProfileUseCase {
    Profile execute(GetProfileQuery query);
}
