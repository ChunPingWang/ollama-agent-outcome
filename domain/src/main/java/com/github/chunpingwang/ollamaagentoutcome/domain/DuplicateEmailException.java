package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試建立或更新已存在的 Email 時拋出。
 */
public class DuplicateEmailException extends RuntimeException {
    
    private final String email;

    public DuplicateEmailException(String email) {
        super("Duplicate email found: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
