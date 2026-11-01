package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

/**
 * 檢查電子郵件是否存在於系統中的外部端口介面。
 */
public interface ExistsByEmailPort {

    /**
     * 檢查指定的電子郵件是否已存在。
     *
     * @param email 要檢查的電子郵件地址
     * @return 若存在則回傳 true，否則為 false
     */
    boolean existsByEmail(String email);
}
