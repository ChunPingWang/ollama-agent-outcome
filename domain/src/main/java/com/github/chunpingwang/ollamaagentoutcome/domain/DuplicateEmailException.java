package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件已存在時拋出。
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
