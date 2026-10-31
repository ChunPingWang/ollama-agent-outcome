package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;

/**
 * 建立個人資料的應用層入口介面。
 */
public interface CreateProfileUseCase {

	/**
	 * 執行建立個人資料的流程。
	 * @param name 姓名
	 * @param email 電子郵件地址
	 * @param age 年齡
	 * @return 建立完成的 Profile 實例
	 */
	Profile execute(Name name, Email email, Age age);
}
