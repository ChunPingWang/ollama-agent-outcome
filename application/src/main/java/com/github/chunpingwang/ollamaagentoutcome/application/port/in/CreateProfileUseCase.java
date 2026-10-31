package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 新增個人資料的使用案例介面。
 */
public interface CreateProfileUseCase {

    /**
     * 執行新增個人資料的操作。
     *
     * @param name 姓名
     * @param email 電子郵件地址
     */
    void execute(String name, String email);
}
