package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * 當簡介超過允許的最大長度時拋出。
 */
public class BioTooLongException extends RuntimeException {

    public BioTooLongException(int maxLength, String actualContent) {
        super("Bio length exceeds maximum allowed of " + maxLength + ". Actual length: " + actualContent.length());
    }
}
