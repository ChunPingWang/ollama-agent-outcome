package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;

/**
 * 更新個人資料的應用層入口介面。
 */
public interface UpdateProfileUseCase {

	/**
	 * 執行更新個人資料的流程。
	 * @param profileId 個人資料識別碼字串
	 * @param name 新姓名
	 * @param email 新電子郵件地址
	 * @param age 新年齡
	 */
	void execute(String profileId, Name name, Email email, Age age);
}
