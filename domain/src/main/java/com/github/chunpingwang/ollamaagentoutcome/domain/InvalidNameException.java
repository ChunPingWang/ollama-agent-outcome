package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 無效姓名例外。
 */
public class InvalidNameException extends IllegalArgumentException {

    public InvalidNameException(String message) {
        super(message);
    }

    public InvalidNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
