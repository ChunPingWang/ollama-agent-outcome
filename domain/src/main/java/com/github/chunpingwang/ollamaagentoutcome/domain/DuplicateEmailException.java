package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試儲存具有已存在電子郵件的個人資料時拋出的領域例外。
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
