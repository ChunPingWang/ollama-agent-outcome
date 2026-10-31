package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當 Bio 長度超過限制時拋出的例外。
 */
public class BioTooLongException extends RuntimeException {
    public BioTooLongException(String message) {
        super(message);
    }
}
