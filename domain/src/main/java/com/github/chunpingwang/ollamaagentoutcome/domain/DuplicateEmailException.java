package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件重複的例外。
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * 建立例外。
     *
     * @param message 錯誤訊息
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
