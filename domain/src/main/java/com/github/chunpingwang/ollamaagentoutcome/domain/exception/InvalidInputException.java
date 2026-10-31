package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 輸入資料驗證失敗時拋出的基礎例外類別。
 */
public class InvalidInputException extends RuntimeException {

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}
}
