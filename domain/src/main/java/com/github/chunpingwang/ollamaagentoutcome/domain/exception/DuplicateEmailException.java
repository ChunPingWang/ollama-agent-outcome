package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 電子郵件重複例外。
 */
public class DuplicateEmailException extends RuntimeException {
    
    private static final String MESSAGE = "Duplicate email address";

    public DuplicateEmailException() {
        super(MESSAGE);
    }
}
