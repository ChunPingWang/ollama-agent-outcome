package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 找不到指定識別碼的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

	public ProfileNotFoundException(String id) {
		super("profile not found with id: " + id);
	}
}
