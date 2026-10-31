package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件格式不符合規範時拋出。
 */
public class InvalidEmailFormatException extends RuntimeException {

    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
