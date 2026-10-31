package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 建立個人資料的應用層 Port 介面。
 */
public interface CreateProfileUseCase {
    /**
     * 建立新的個人資料。
     *
     * @param request 包含電子郵件、姓名與簡介的請求資料
     */
    void create(CreateProfileRequest request);

    /**
     * 建立個人資料的請求 DTO。
     *
     * @param email 電子郵件
     * @param name  姓名
     * @param bio   簡介
     */
    record CreateProfileRequest(String email, String name, String bio) {}
}
