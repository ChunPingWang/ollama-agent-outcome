package com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception;

/**
 * Email 格式錯誤時拋出的領域例外。
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String email) {
        super("Invalid email format: " + email);
    }
}
