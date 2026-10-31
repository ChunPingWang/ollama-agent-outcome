package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 無效顯示名稱例外。
 */
public class InvalidDisplayNameException extends IllegalArgumentException {

    private static final String MESSAGE = "Invalid display name length or content";

    public InvalidDisplayNameException() {
        super(MESSAGE);
    }
    
    public InvalidDisplayNameException(String message) {
        super(message);
    }
}
