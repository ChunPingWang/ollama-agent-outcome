package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件重複時拋出的例外。
 */
public class DuplicateEmailException extends InvalidInputException {

    public DuplicateEmailException() {
        super("email already exists");
    }
}
