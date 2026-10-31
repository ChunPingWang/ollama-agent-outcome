package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件格式不符合 RFC 5322 基本規範時拋出。
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super("無效的電子郵件格式");
    }
}
