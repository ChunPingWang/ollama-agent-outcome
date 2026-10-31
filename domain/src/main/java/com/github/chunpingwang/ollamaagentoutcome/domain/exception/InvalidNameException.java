package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 姓名無效例外。
 */
public class InvalidNameException extends RuntimeException {

    public InvalidNameException(String message) {
        super(message);
    }
}
