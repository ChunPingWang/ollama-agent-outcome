package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;

/**
 * 查詢個人資料的應用層入口介面。
 */
public interface GetProfileUseCase {

	/**
	 * 根據識別碼取得個人資料。
	 * @param profileId 個人資料識別碼字串
	 * @return 找到的 Profile 實例
	 * @throws com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException 若找不到資料
	 */
	Profile execute(String profileId);
}
