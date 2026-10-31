package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當姓名長度或內容不合規時拋出。
 */
public class InvalidNameException extends RuntimeException {

    public InvalidNameException(String message) {
        super(message);
    }
}
