package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當 DisplayName Value Object 長度或內容無效時拋出的例外。
 */
public class InvalidDisplayNameException extends RuntimeException {
    public InvalidDisplayNameException(String message) {
        super(message);
    }
}
