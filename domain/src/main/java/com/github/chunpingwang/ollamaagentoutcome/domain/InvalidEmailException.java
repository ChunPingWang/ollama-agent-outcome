package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當電子郵件格式無效時拋出的領域例外。
 */
public class InvalidEmailException extends RuntimeException {
    /**
     * 建立新的例外。
     *
     * @param message 錯誤訊息
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}
