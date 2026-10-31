package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試儲存已存在的電子郵件地址時拋出。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("電子郵件已重複");
    }
}
