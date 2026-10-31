package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當輸入的電子郵件格式無效時拋出。
 */
public class InvalidEmailFormatException extends IllegalArgumentException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
