package com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception;

/**
 * Bio 內容無效時拋出的領域例外。
 */
public class InvalidBioException extends RuntimeException {

    public InvalidBioException() {
        super("Bio content is invalid");
    }
    
    public InvalidBioException(int maxLength) {
        super("Bio must not exceed " + maxLength + " characters");
    }
}
