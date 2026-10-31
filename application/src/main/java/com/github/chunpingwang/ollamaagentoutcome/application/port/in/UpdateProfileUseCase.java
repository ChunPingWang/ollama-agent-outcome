package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 更新個人資料的應用層 Port 介面。
 */
public interface UpdateProfileUseCase {
    /**
     * 更新個人資料。
     *
     * @param profileId 個人資料 ID
     * @param request   包含電子郵件、姓名與簡介的請求資料
     * @return 更新後的個人資料回應資料
     */
    GetProfileUseCase.ProfileResponse update(String profileId, UpdateProfileRequest request);

    /**
     * 更新個人資料的請求 DTO。
     *
     * @param email 電子郵件
     * @param name  姓名
     * @param bio   簡介
     */
    record UpdateProfileRequest(String email, String name, String bio) {}
}
