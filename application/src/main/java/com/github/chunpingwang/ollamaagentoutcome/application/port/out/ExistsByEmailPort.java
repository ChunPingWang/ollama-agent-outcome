package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;

/**
 * 檢查電子郵件是否存在於系統中的外向端口。
 */
public interface ExistsByEmailPort {
    /**
     * 檢查指定的電子郵件是否已存在。
     * @param email 要檢查的電子郵件值物件
     * @return 若存在則為 true，否則為 false
     */
    boolean existsByEmail(Email email);
}
