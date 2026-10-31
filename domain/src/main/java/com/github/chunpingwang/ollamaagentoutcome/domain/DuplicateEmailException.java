package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試使用已存在的電子郵件時拋出。
 */
public class DuplicateEmailException extends InvalidProfileException {

    public DuplicateEmailException() {
        super("email already exists");
    }
}
