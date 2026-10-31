package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當年齡無效（非正整數）時拋出的例外。
 */
public class InvalidAgeException extends RuntimeException {

    public InvalidAgeException(String message) {
        super(message);
    }
}
