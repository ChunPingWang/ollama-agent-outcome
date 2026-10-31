package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 無效 Email 格式例外。
 */
public class InvalidEmailException extends IllegalArgumentException {

    private static final String MESSAGE = "Invalid email format";

    public InvalidEmailException() {
        super(MESSAGE);
    }
    
    public InvalidEmailException(String message) {
        super(message);
    }
}
