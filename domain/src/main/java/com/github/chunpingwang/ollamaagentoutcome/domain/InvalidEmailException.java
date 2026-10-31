package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當提供的電子郵件格式無效時拋出。
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
