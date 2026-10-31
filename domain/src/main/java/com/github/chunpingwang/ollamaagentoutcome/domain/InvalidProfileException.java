package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當個人資料資料不符合業務規則時拋出。
 */
public class InvalidProfileException extends RuntimeException {
    public InvalidProfileException(String message) {
        super(message);
    }
}
