package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當姓名無效時拋出。
 */
public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
