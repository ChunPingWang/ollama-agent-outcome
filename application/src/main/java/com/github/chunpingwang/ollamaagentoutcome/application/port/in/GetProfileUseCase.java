package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 查詢個人資料的應用層 Port 介面。
 */
public interface GetProfileUseCase {
    /**
     * 根據 ID 查詢個人資料。
     *
     * @param profileId 個人資料 ID
     * @return 個人資料回應資料
     */
    ProfileResponse get(String profileId);

    /**
     * 個人資料的回應 DTO。
     *
     * @param id    個人資料 ID
     * @param email 電子郵件
     * @param name  姓名
     * @param bio   簡介
     */
    record ProfileResponse(String id, String email, String name, String bio) {}
}
