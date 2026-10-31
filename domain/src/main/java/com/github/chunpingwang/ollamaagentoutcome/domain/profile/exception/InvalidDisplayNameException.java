package com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception;

/**
 * Display Name 格式或長度無效時拋出的領域例外。
 */
public class InvalidDisplayNameException extends RuntimeException {

    public InvalidDisplayNameException() {
        super("Display name is invalid");
    }
    
    public InvalidDisplayNameException(int maxLength) {
        super("Display name must not exceed " + maxLength + " characters");
    }
}
