package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 無效電子郵件例外。
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
