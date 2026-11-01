package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件格式無效時拋出的例外。
 */
public class InvalidEmailException extends IllegalArgumentException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
