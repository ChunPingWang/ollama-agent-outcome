package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 當查詢指定識別碼卻找不到資料時拋出。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String id) {
        super("找不到個人資料：" + id);
    }
}
