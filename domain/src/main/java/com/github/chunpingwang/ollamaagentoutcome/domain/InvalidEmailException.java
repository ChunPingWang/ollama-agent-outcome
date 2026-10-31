package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 無效電子郵件格式例外。
 */
public class InvalidEmailException extends IllegalArgumentException {

    public InvalidEmailException(String message) {
        super(message);
    }

    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
