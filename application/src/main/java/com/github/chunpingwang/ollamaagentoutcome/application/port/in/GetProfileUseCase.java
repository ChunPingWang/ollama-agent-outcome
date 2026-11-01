package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * 查詢單一個人資料的應用層入口介面。
 */
public interface GetProfileUseCase {

    /**
     * 根據識別碼查詢個人資料。若不存在則拋出例外。
     *
     * @param id 個人資料的唯一識別碼
     * @return 找到的個人資料回應物件
     */
    GetProfileResponse findById(String id);

    /**
     * 查詢結果的資料傳輸物件。
     */
    record GetProfileResponse(String id, String email, String displayName) {}
}
