package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當顯示名稱無效（如空白）時拋出的領域例外。
 */
public class InvalidDisplayNameException extends RuntimeException {

    public InvalidDisplayNameException(String message) {
        super(message);
    }
}
