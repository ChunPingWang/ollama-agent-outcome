package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 無效姓名例外。
 */
public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
