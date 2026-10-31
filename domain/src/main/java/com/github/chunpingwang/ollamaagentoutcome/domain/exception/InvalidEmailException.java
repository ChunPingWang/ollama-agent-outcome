package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當 Email Value Object 格式無效時拋出的例外。
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
