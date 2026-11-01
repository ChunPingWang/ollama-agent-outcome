package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢或操作不存在的 ID 時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String id) {
        super("找不到個人資料：" + id);
    }
}
