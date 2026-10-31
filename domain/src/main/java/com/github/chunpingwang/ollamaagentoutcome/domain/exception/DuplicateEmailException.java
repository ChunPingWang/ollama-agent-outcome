package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當嘗試保存已存在的電子郵件時拋出的例外。
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
