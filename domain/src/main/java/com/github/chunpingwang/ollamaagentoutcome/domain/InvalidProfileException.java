package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料相關的基礎例外類別。
 */
public class InvalidProfileException extends RuntimeException {

    public InvalidProfileException(String message) {
        super(message);
    }

    public InvalidProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
