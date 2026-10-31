package com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception;

/**
 * 電子郵件已被佔用時拋出的領域例外。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Duplicate email: " + email);
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
