package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件重複時拋出的例外。
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
