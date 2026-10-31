package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件已存在於系統中時拋出的例外。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }
}
