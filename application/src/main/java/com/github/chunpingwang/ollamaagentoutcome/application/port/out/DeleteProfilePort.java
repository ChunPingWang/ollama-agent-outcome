package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的外部端口。
 */
public interface DeleteProfilePort {

    /**
     * 根據識別碼刪除個人資料。
     *
     * @param id 個人資料識別碼
     */
    void deleteById(ProfileId id);
}
