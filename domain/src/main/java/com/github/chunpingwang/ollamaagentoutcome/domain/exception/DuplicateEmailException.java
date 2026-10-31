package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當儲存資料時發現電子郵件已存在於系統中時拋出。
 */
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("Duplicate email address: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
