package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當建立或更新資料時，發現電子郵件已存在於系統中時拋出。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
