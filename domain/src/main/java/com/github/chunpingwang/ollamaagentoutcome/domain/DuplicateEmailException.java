package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試使用已存在的電子郵件建立個人資料時拋出的領域例外。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Duplicate email");
    }
}
