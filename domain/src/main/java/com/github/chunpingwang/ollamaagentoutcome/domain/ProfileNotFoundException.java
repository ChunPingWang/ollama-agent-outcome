package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 找不到個人資料的例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    /**
     * 建立例外。
     *
     * @param message 錯誤訊息
     */
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
