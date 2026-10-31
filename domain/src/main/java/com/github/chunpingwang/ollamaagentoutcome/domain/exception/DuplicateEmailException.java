package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當嘗試建立或更新個人資料，但電子郵件已存在時拋出。
 */
public class DuplicateEmailException extends RuntimeException {

	private static final String MESSAGE = "email already exists";

	public DuplicateEmailException() {
		super(MESSAGE);
	}

	public DuplicateEmailException(String message) {
		super(message);
	}
}
