package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 電子郵件格式無效例外。
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
