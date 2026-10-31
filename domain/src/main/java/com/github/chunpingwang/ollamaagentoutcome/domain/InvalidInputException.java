package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 輸入數據不符合業務規則時拋出的例外。
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
