package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 刪除個人資料的應用層入口介面。
 */
public interface DeleteProfileUseCase {

	/**
	 * 執行刪除個人資料的流程。
	 * @param profileId 個人資料識別碼字串
	 */
	void execute(String profileId);
}
