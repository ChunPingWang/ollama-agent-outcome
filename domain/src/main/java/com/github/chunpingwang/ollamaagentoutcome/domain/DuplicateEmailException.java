package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件重複例外。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
