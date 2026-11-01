package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

/**
 * 更新個人資料顯示名稱的請求 DTO。
 */
public class UpdateProfileRequest {

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
