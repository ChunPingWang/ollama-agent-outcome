package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;

/**
 * 電子郵件重複例外。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(Email email) {
        super("Email already exists: " + email.value());
    }
}
