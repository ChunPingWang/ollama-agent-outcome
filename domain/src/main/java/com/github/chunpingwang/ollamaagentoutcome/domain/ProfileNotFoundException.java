package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 當查詢的個人資料 ID 不存在時拋出的領域例外。
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String idValue) {
        super("找不到 ID 為 " + idValue + " 的個人資料");
    }
    
    public ProfileNotFoundException(UUID id) {
        this(id.toString());
    }
}
