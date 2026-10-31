package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當年齡無效時拋出。
 */
public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String message) {
        super(message);
    }
}
