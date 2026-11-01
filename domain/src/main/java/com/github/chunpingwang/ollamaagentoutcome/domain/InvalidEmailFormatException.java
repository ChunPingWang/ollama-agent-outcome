package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當輸入的電子郵件格式不符合規範時所拋出的例外。
 */
public class InvalidEmailFormatException extends RuntimeException {

    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
