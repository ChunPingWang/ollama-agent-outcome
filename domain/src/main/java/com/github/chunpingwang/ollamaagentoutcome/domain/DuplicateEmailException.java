package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試建立或更新已存在的電子郵件時拋出。
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
