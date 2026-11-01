package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當輸入格式不符合 Email 規範時拋出。
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
