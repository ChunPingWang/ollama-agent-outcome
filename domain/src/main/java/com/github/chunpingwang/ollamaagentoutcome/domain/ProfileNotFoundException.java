package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或更新/刪除操作找不到指定 ID 的個人資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
