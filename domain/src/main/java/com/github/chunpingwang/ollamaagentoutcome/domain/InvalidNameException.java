package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當姓名不符合規範時拋出。
 */
public class InvalidNameException extends IllegalArgumentException {
    public InvalidNameException(String message) {
        super(message);
    }
}
