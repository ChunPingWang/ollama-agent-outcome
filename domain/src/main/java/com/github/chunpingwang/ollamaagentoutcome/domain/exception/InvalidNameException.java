package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 姓名無效時拋出的例外。
 */
public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
