package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

/**
 * 刪除個人資料的 Outbound Port。
 */
public interface DeleteProfilePort {
    void deleteById(String id);
}
