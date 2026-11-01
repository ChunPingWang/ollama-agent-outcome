package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當嘗試儲存已存在的電子郵件時拋出的領域例外。
 */
public class DuplicateEmailException extends RuntimeException {
    /**
     * 建立新的例外。
     *
     * @param message 錯誤訊息
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
