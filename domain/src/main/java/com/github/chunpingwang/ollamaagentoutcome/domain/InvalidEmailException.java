package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當 Email 格式無效時拋出的例外。
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
